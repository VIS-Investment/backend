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
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        FetchStrategy strategy = fetchStrategySelector.select(range);
        List<StockInfo> stockInfos = stockInfoRepository.findAll();

        int batchSize = 2000;
        for (int i = 0; i < stockInfos.size(); i += batchSize) {
            int end = Math.min(i + batchSize, stockInfos.size());
            List<StockInfo> batch = stockInfos.subList(i, end);

            List<StockPrices> data = strategy.fetch(batch, range);
            inserter.batchInsertIgnore(data);

            log.info("Batch inserted: {} ~ {}", i, end);
        }

        stopWatch.stop();
        log.info("실행 시간(ms): " + stopWatch.prettyPrint());
    }
}