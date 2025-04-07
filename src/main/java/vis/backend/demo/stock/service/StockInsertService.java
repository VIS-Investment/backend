package vis.backend.demo.stock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import vis.backend.demo.stock.api.StockFetcher;
import vis.backend.demo.stock.converter.StockPricesConverter;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;
import vis.backend.demo.stock.dto.StockDto;
import vis.backend.demo.stock.repository.StockInfoRepository;
import vis.backend.demo.stock.repository.StockPricesCompositeIdxRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockInsertService {

    private final StockPricesCompositeIdxRepository stockPricesRepository;
    private final StockBatchInserter stockBatchInserter;
    private final StockInfoRepository stockInfoRepository;
    private final StockFetcher fetcher;

    public void fetchAndInsert(String range) {
        List<StockInfo> stockInfos = stockInfoRepository.findAll();
        List<StockPricesCompositeIdx> allEntities = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            stopWatch.start("fetch");
            List<Callable<List<StockPricesCompositeIdx>>> tasks = stockInfos.stream()
                    .map(info -> (Callable<List<StockPricesCompositeIdx>>) () -> {
                        List<StockDto.StockPricesSimpleDto> dtos = fetcher.fetch(info.getTicker(), range);
                        return dtos.stream()
                                .map(dto -> StockPricesConverter.toEntity(dto, info))
                                .collect(Collectors.toList());
                    })
                    .collect(Collectors.toList());

            List<Future<List<StockPricesCompositeIdx>>> futures = executor.invokeAll(tasks);

            for (Future<List<StockPricesCompositeIdx>> future : futures) {
                try {
                    allEntities.addAll(future.get(10, TimeUnit.SECONDS));
                } catch (TimeoutException e) {
                    System.err.println("Timeout occurred for a task: " + e.getMessage());
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("Error occurred while processing a task: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            stopWatch.stop();

            stopWatch.start("insert");
            if (!allEntities.isEmpty()) {
                stockBatchInserter.batchInsertIgnore(allEntities);
            }
            stopWatch.stop();
            log.info("실행 시간(ms): " + stopWatch.prettyPrint());


        } catch (Exception e) {
            throw new RuntimeException("Error during virtual thread execution or batch insert", e);
        }
    }

    public void fetchAndInsertTest(String range) {
        // 테스트용 AAPL 티커 하나만 처리
        Optional<StockInfo> aaplInfoOpt = stockInfoRepository.findAll().stream()
                .filter(info -> "AAPL".equalsIgnoreCase(info.getTicker()))
                .findFirst();

        if (aaplInfoOpt.isEmpty()) {
            System.err.println("AAPL ticker not found in stock_info table.");
            return;
        }

        StockInfo aaplInfo = aaplInfoOpt.get();

        try {
            List<StockDto.StockPricesSimpleDto> dtos = fetcher.fetch(aaplInfo.getTicker(), range);

            List<StockPricesCompositeIdx> entities = dtos.stream()
                    .map(dto -> StockPricesConverter.toEntity(dto, aaplInfo))
                    .collect(Collectors.toList());

            if (!entities.isEmpty()) {
                stockPricesRepository.saveAll(entities);
                System.out.println("AAPL data inserted successfully. Rows: " + entities.size());
            } else {
                System.out.println("No AAPL data fetched to insert.");
            }

        } catch (Exception e) {
            System.err.println("Error during AAPL fetch & insert: " + e.getMessage());
        }
    }

}
