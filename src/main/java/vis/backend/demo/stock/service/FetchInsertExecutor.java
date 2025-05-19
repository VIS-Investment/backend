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
    private final FetchStrategySelector fetchStrategySelector;
    private final StockBatchInserter inserter;

    public void execute(String range) {
        StopWatch totalStopWatch = new StopWatch("전체 실행");
        StopWatch fetchStopWatch = new StopWatch("Fetch 누적");
        StopWatch insertStopWatch = new StopWatch("Insert 누적");

        totalStopWatch.start();

        FetchStrategy strategy = fetchStrategySelector.select(range);
        List<StockInfo> stockInfos = stockInfoRepository.findAll();

        int batchSize = 100;
        for (int i = 0; i < stockInfos.size(); i += batchSize) {
            int end = Math.min(i + batchSize, stockInfos.size());
            List<StockInfo> batch = stockInfos.subList(i, end);

            fetchStopWatch.start();
            List<StockPrices> data = strategy.fetch(batch, range);
            fetchStopWatch.stop();

            insertStopWatch.start();
            inserter.batchInsertIgnore(data);
            insertStopWatch.stop();

            log.info("Batch inserted: {} ~ {}", i, end);
        }

        totalStopWatch.stop();

        log.info("\n▶ 전체 실행 시간:\n{}", totalStopWatch.prettyPrint());
        log.info("\n▶ Fetch 시간 합계:\n{}", fetchStopWatch.prettyPrint());
        log.info("\n▶ Insert 시간 합계:\n{}", insertStopWatch.prettyPrint());
    }
}