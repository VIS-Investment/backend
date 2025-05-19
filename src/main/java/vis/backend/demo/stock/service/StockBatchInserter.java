package vis.backend.demo.stock.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;

@Service
@RequiredArgsConstructor
public class StockBatchInserter {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 1만 건씩 INSERT IGNORE Batch
     */
    public void batchInsertIgnore(List<StockPricesCompositeIdx> list) {

        String sql = """
                    INSERT IGNORE INTO stock_prices_composite_idx
                      (trade_date, ticker_id,
                       open_price, close_price, high_price, low_price, volume)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, list, 10_000, (ps, entity) -> {
            ps.setDate(1, java.sql.Date.valueOf(entity.getTradeDate()));
            ps.setInt(2, entity.getTickerId());
            ps.setBigDecimal(3, entity.getOpenPrice());
            ps.setBigDecimal(4, entity.getClosePrice());
            ps.setBigDecimal(5, entity.getHighPrice());
            ps.setBigDecimal(6, entity.getLowPrice());
            ps.setLong(7, entity.getVolume());
        });
    }
}
