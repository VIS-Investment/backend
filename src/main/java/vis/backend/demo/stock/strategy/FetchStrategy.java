package vis.backend.demo.stock.strategy;

import java.util.List;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPrices;

public interface FetchStrategy {
    List<StockPrices> fetch(List<StockInfo> infos, String range);

    String getType(); // "virtual" or "webclient"
}
