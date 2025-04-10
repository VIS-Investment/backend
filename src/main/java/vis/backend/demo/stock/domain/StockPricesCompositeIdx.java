package vis.backend.demo.stock.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
        name = "stock_prices_composite_idx",
        indexes = {
                @Index(name = "idx_date", columnList = "trade_date")
        }
)
public class StockPricesCompositeIdx {

    @EmbeddedId
    private StockPricesId id; // 복합 PK 사용

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("stockInfo")
    @JoinColumn(name = "ticker_id", nullable = false)
    private StockInfo stockInfo;

    private BigDecimal openPrice;

    private BigDecimal closePrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    private Long volume;
}
