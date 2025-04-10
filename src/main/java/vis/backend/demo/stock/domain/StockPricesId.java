package vis.backend.demo.stock.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;

@Embeddable
public class StockPricesId implements Serializable {
    private Integer stockInfo;
    @Getter
    private LocalDate tradeDate;

    public StockPricesId() {
    }

    public StockPricesId(Integer stockInfo, LocalDate tradeDate) {
        this.stockInfo = stockInfo;
        this.tradeDate = tradeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StockPricesId that = (StockPricesId) o;
        return stockInfo.equals(that.stockInfo) && tradeDate.equals(that.tradeDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockInfo, tradeDate);
    }
}
