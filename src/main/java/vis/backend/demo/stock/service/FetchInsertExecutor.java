package vis.backend.demo.stock.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPrices;
import vis.backend.demo.stock.repository.StockInfoRepository;
import vis.backend.demo.stock.strategy.FetchStrategy;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchInsertExecutor {
    private final StockInfoRepository stockInfoRepository;
    private final FetchStrategySelector selector;
    private final StockBatchInserter inserter;

    public void execute(String range) {
        StopWatch stopWatch = new StopWatch();

        FetchStrategy strategy = selector.select(range);
        List<StockInfo> stockInfos = stockInfoRepository.findAll();

        stopWatch.start("fetch");
        List<StockPrices> data = strategy.fetch(stockInfos, range);
        stopWatch.stop();
        log.info("fetch end");

        stopWatch.start("insert");
        inserter.batchInsertIgnore(data);
        stopWatch.stop();

        log.info("실행 시간(ms): " + stopWatch.prettyPrint());
    }
}

// 사전 적재
//package vis.backend.demo.stock.service;
//
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StopWatch;
//import vis.backend.demo.stock.domain.StockInfo;
//import vis.backend.demo.stock.domain.StockPrices;
//import vis.backend.demo.stock.repository.StockInfoRepository;
//import vis.backend.demo.stock.strategy.FetchStrategy;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class FetchInsertExecutor {
//    private final StockInfoRepository stockInfoRepository;
//    private final FetchStrategySelector selector;
//    private final StockBatchInserter inserter;
//
//    private static final int BATCH_SIZE = 100;
//    private static final long DELAY_MS = 10000; // 배치 사이에 10초 쉬기
//    // 5 (600)x
//
//    public void execute(String range) {
//        StopWatch stopWatch = new StopWatch();
//        FetchStrategy strategy = selector.select(range);
//        List<StockInfo> stockInfos = stockInfoRepository.findAll();
//
//        stopWatch.start("total");
//
//        for (int i = 0; i < stockInfos.size(); i += BATCH_SIZE) {
//            int end = Math.min(i + BATCH_SIZE, stockInfos.size());
//            List<StockInfo> batch = stockInfos.subList(i, end);
//
//            StopWatch batchWatch = new StopWatch();
//            batchWatch.start("fetch");
//            List<StockPrices> data = strategy.fetch(batch, range);
//            batchWatch.stop();
//
//            batchWatch.start("insert");
//            inserter.batchInsertIgnore(data);
//            batchWatch.stop();
//
//            log.info("Batch {} ~ {} 처리 완료: {}", i, end, batchWatch.prettyPrint());
//
//            try {
//                Thread.sleep(DELAY_MS);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//        stopWatch.stop();
//        log.info("실행 시간(ms): " + stopWatch.prettyPrint());
//    }
//}