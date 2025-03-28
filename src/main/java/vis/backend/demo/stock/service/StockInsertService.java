package vis.backend.demo.stock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vis.backend.demo.stock.api.StockFetcher;
import vis.backend.demo.stock.converter.StockPricesConverter;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPrices;
import vis.backend.demo.stock.dto.StockDto;
import vis.backend.demo.stock.repository.StockInfoRepository;
import vis.backend.demo.stock.repository.StockPricesRepository;

@Service
@RequiredArgsConstructor
public class StockInsertService {

    private final StockPricesRepository stockPricesRepository;
    private final StockInfoRepository stockInfoRepository;
    private final StockFetcher fetcher;
    private final StockPricesConverter converter = new StockPricesConverter();

    public void fetchAndInsert(String range) {
        List<StockInfo> stockInfos = stockInfoRepository.findAll();
        List<StockPrices> allEntities = new ArrayList<>();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<List<StockPrices>>> tasks = stockInfos.stream()
                    .map(info -> (Callable<List<StockPrices>>) () -> {
                        List<StockDto.StockPricesSimpleDto> dtos = fetcher.fetch(info.getTicker(), range);
                        return dtos.stream()
                                .map(dto -> converter.toEntity(dto, info))
                                .collect(Collectors.toList());
                    })
                    .collect(Collectors.toList());

            List<Future<List<StockPrices>>> futures = executor.invokeAll(tasks);

            for (Future<List<StockPrices>> future : futures) {
                try {
                    allEntities.addAll(future.get(10, TimeUnit.SECONDS)); // 타임아웃 처리 추가
                } catch (TimeoutException e) {
                    System.err.println("Timeout occurred for a task: " + e.getMessage());
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("Error occurred while processing a task: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }

            if (!allEntities.isEmpty()) {
                stockPricesRepository.saveAll(allEntities);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error during virtual thread execution or batch insert", e);
        }
    }
}
