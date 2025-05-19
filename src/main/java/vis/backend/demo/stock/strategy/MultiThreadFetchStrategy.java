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
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;
import vis.backend.demo.stock.dto.StockDto;

@Slf4j
@Component("fixed")
@RequiredArgsConstructor
public class MultiThreadFetchStrategy implements FetchStrategy {

    private final VirtualThreadFetcher fetcher;
    private final FetchRetry fetchRetry;

    @Override
    public List<StockPricesCompositeIdx> fetch(List<StockInfo> infos, String range) {
        List<StockPricesCompositeIdx> results = new ArrayList<>();
        double failedCount = 0.0;
        List<String> failedTickers = new ArrayList<>();

        int threadPoolSize = Math.min(32, infos.size());
        try (var executor = Executors.newFixedThreadPool(threadPoolSize)) {
            Semaphore semaphore = new Semaphore(1000);

            List<Callable<List<StockPricesCompositeIdx>>> tasks = infos.stream()
                    .map(info -> (Callable<List<StockPricesCompositeIdx>>) () -> {
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

            List<Future<List<StockPricesCompositeIdx>>> futures = executor.invokeAll(tasks);

            for (int i = 0; i < futures.size(); i++) {
                StockInfo info = infos.get(i);
                try {
                    results.addAll(futures.get(i).get(10, TimeUnit.SECONDS));
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message.contains("No data found") || message.contains("404 Not Found")) {
                        log.error(e.getMessage());
                    } else {
                        log.error("FixedThread task failed: " + e.getMessage());
                        failedCount++;
                        failedTickers.add(info.getTicker());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("FixedThread execution failed", e);
        }

        double total = infos.size();
        double failRate = (failedCount / total) * 100;
        double successRate = ((total - failedCount) / total) * 100;

        log.info("FixedThreadFetch completed. Total: {}, Failed: {} ({}%), Success: {} ({}%)",
                (int) total,
                (int) failedCount,
                String.format("%.2f", failRate),
                (int) (total - failedCount),
                String.format("%.2f", successRate)
        );

        if (!failedTickers.isEmpty()) {
            log.warn("Failed tickers: {}", String.join(", ", failedTickers));
        }

        return results;
    }

    @Override
    public String getType() {
        return "fixed";
    }
}