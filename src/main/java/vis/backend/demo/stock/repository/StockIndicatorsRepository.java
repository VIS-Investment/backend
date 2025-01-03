package vis.backend.demo.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vis.backend.demo.stock.domain.StockIndicators;

public interface StockIndicatorsRepository extends JpaRepository<StockIndicators, Long> {

}
