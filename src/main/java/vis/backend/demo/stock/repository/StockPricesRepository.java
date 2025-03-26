package vis.backend.demo.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vis.backend.demo.stock.domain.StockPrices;

public interface StockPricesRepository extends JpaRepository<StockPrices, Long> {

}