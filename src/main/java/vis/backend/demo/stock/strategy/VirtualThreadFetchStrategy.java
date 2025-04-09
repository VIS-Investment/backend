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
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;
import vis.backend.demo.stock.dto.StockDto;

@Component("virtual")
@RequiredArgsConstructor
public class VirtualThreadFetchStrategy implements FetchStrategy {

    private final VirtualThreadFetcher fetcher;

    @Override
    public List<StockPricesCompositeIdx> fetch(List<StockInfo> infos, String range) {
        List<StockPricesCompositeIdx> results = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<List<StockPricesCompositeIdx>>> tasks = infos.stream()
                    .map(info -> (Callable<List<StockPricesCompositeIdx>>) () -> {
                        List<StockDto.StockPricesSimpleDto> dtos = fetcher.fetch(info.getTicker(), range);
                        return dtos.stream()
                                .map(dto -> StockPricesConverter.toEntity(dto, info))
                                .toList();
                    })
                    .toList();

            List<Future<List<StockPricesCompositeIdx>>> futures = executor.invokeAll(tasks);

            for (Future<List<StockPricesCompositeIdx>> future : futures) {
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