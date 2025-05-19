package vis.backend.demo.stock.domain;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // equals/hashCode 포함
@NoArgsConstructor
@AllArgsConstructor
public class StockPricesId implements Serializable {
    private LocalDate tradeDate;   // PK 1
    private Integer tickerId;     // PK 2

}
