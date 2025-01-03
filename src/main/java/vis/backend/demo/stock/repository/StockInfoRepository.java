package vis.backend.demo.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vis.backend.demo.stock.domain.StockInfo;

public interface StockInfoRepository extends JpaRepository<StockInfo, Integer> {
    StockInfo findByTicker(String ticker);

}
