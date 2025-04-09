package vis.backend.demo.stock.strategy;

import java.util.List;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;

public interface FetchStrategy {
    List<StockPricesCompositeIdx> fetch(List<StockInfo> infos, String range);

    String getType(); // "virtual" or "webclient"
}
