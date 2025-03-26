package vis.backend.demo.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "stock_prices")

public class StockPrices {
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

    public void updatePrices(BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low, Long volume) {
        this.openPrice = open;
        this.closePrice = close;
        this.highPrice = high;
        this.lowPrice = low;
        this.volume = volume;
    }
}
