package vis.backend.demo.stock.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "stock_prices_composite_idx",
        indexes = @Index(name = "idx_ticker", columnList = "ticker_id")
)
@IdClass(StockPricesId.class)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class StockPricesCompositeIdx {

    @Id
    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;        // PK 1

    @Id
    @Column(name = "ticker_id", nullable = false)
    private Integer tickerId;           // PK 2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id",
            insertable = false,
            updatable = false)
    private StockInfo stockInfo;

    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private Long volume;
}