package vis.backend.demo.stock.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vis.backend.demo.stock.converter.StockPricesConverter;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPrices;
import vis.backend.demo.stock.dto.StockDto;

@Component("virtual")
@RequiredArgsConstructor
public class VirtualThreadFetchStrategy implements FetchStrategy {

    private final VirtualThreadFetcher fetcher;

    @Override
    public List<StockPrices> fetch(List<StockInfo> infos, String range) {
        List<StockPrices> results = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<List<StockPrices>>> tasks = infos.stream()
                    .map(info -> (Callable<List<StockPrices>>) () -> {
                        List<StockDto.StockPricesSimpleDto> dtos = fetcher.fetch(info.getTicker(), range);
                        return dtos.stream()
                                .map(dto -> StockPricesConverter.toEntity(dto, info))
                                .toList();
                    })
                    .toList();

            List<Future<List<StockPrices>>> futures = executor.invokeAll(tasks);

            for (Future<List<StockPrices>> future : futures) {
                try {
                    results.addAll(future.get(10, TimeUnit.SECONDS));
                } catch (Exception e) {
                    System.err.println("VirtualThread task failed: " + e.getMessage());
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