package vis.backend.demo.stock.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vis.backend.demo.stock.domain.StockInfo;

public interface StockInfoRepository extends JpaRepository<StockInfo, Integer> {
    StockInfo findByTicker(String ticker);

    @Query("select s.ticker from StockInfo s")
    List<String> findAllTickers();

}
