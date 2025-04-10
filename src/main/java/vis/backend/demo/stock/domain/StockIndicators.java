package vis.backend.demo.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(
        name = "stock_indicators",
        indexes = {
                @Index(name = "idx_date_ticker_id", columnList = "trade_date, ticker_id")
        }
)
public class StockIndicators {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id", nullable = false)
    private StockInfo stockInfo;

    @Column(nullable = false)
    private String tradeDate;

    private BigDecimal rsi;

    private BigDecimal obv;

    private BigDecimal pvt;

    private BigDecimal stochK;

    private BigDecimal stochD;

    private BigDecimal bollingerUp;

    private BigDecimal bollingerDown;

    private BigDecimal rsiBollingerUp;

    private BigDecimal rsiBollingerDown;

    private BigDecimal obvBollingerUp;

    private BigDecimal obvBollingerDown;

    private BigDecimal pvtBollingerUp;

    private BigDecimal pvtBollingerDown;
}
