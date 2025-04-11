package vis.backend.demo.stock.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vis.backend.demo.global.utils.FetchRetry;
import vis.backend.demo.stock.converter.StockPricesConverter;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPrices;
import vis.backend.demo.stock.dto.StockDto;

@Slf4j
@Component("virtual")
@RequiredArgsConstructor
public class VirtualThreadFetchStrategy implements FetchStrategy {

    private final VirtualThreadFetcher fetcher;
    private final FetchRetry fetchRetry;

    @Override
    public List<StockPrices> fetch(List<StockInfo> infos, String range) {
        List<StockPrices> results = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Semaphore semaphore = new Semaphore(1000);

            List<Callable<List<StockPrices>>> tasks = infos.stream()
                    .map(info -> (Callable<List<StockPrices>>) () -> {
                        semaphore.acquire();
                        try {
                            List<StockDto.StockPricesSimpleDto> dtos = fetchRetry.retry(3, 2000,
                                    () -> fetcher.fetch(info.getTicker(), range), info.getTicker());
                            return dtos.stream()
                                    .map(dto -> StockPricesConverter.toEntity(dto, info))
                                    .toList();
                        } finally {
                            semaphore.release();
                        }
                    })
                    .toList();

            List<Future<List<StockPrices>>> futures = executor.invokeAll(tasks);

            for (Future<List<StockPrices>> future : futures) {
                try {
                    results.addAll(future.get(10, TimeUnit.SECONDS));
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message.contains("No data found") || message.contains("404 Not Found")) {
                        log.error(e.getMessage());
                    } else {
                        log.error("VirtualThread task failed: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("VirtualThread execution failed", e);
        }

        return results;
    }

    @Override
    public String getType() {
        return "virtual";
    }
}