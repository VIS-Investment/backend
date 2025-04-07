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

    public void batchInsertIgnore(List<StockPricesCompositeIdx> list) {
        String sql = """
                    INSERT IGNORE INTO stock_prices_composite_idx
                    (ticker_id, trade_date, open_price, close_price, high_price, low_price, volume)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, list, 10000, (ps, entity) -> {
            ps.setInt(1, entity.getStockInfo().getId());
            ps.setDate(2, java.sql.Date.valueOf(entity.getId().getTradeDate()));
            ps.setBigDecimal(3, entity.getOpenPrice());
            ps.setBigDecimal(4, entity.getClosePrice());
            ps.setBigDecimal(5, entity.getHighPrice());
            ps.setBigDecimal(6, entity.getLowPrice());
            ps.setLong(7, entity.getVolume());
        });
    }
}
