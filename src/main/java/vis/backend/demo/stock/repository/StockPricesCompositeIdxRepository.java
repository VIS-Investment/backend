package vis.backend.demo.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;
import vis.backend.demo.stock.domain.StockPricesId;

public interface StockPricesCompositeIdxRepository extends JpaRepository<StockPricesCompositeIdx, StockPricesId> {
}
