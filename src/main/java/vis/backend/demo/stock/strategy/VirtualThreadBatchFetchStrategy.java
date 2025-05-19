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

@Slf4j
@Component("batch")
@RequiredArgsConstructor
public class VirtualThreadBatchFetchStrategy implements FetchStrategy {

    private final VirtualThreadFetcher fetcher;
    private final FetchRetry fetchRetry;

    @Override
    public List<StockPrices> fetch(List<StockInfo> infos, String range) {
        List<StockPrices> results = new ArrayList<>();
        double failedCount = 0.0;
        List<String> failedTickers = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Semaphore semaphore = new Semaphore(10);

            List<Callable<List<StockPrices>>> tasks = infos.stream()
                    .map(info -> (Callable<List<StockPrices>>) () -> {
                        semaphore.acquire();
                        try {
                            var dtos = fetchRetry.retry(3, 2000,
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

            for (int i = 0; i < futures.size(); i++) {
                StockInfo info = infos.get(i);
                try {
                    results.addAll(futures.get(i).get(10, TimeUnit.SECONDS));
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message.contains("No data found") || message.contains("404 Not Found")) {
                        log.error(message);
                    } else {
                        log.error("VirtualThread task failed: {}", message);
                        failedCount++;
                        failedTickers.add(info.getTicker());
                    }
                }
            }

            Thread.sleep(1500);

        } catch (Exception e) {
            throw new RuntimeException("VirtualThread execution failed", e);
        }

        double total = infos.size();
        double failRate = (failedCount / total) * 100;
        double successRate = ((total - failedCount) / total) * 100;

        log.info("VirtualThreadFetch completed. Total: {}, Failed: {} ({}%), Success: {} ({}%)",
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
        return "batch";
    }
}