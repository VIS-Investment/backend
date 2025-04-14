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