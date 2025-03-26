package vis.backend.demo.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Table(name = "stock_price")
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id", nullable = false)
    private StockInfo stockInfo;

    @Column(nullable = false)
    private String tradeDate;

    private BigDecimal openPrice;

    private BigDecimal closePrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    private Long volume;
}
