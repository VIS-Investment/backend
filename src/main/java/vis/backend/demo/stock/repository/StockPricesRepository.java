package vis.backend.demo.stock.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vis.backend.demo.stock.domain.StockPrices;

public interface StockPricesRepository extends JpaRepository<StockPrices, Long> {
    Optional<StockPrices> findByStockInfo_TickerAndTradeDate(String ticker, LocalDate tradeDate);
}