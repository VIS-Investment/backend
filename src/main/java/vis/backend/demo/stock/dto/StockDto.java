package vis.backend.demo.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StockDto {
    @Schema(description = "StockPricesSimpleDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockPricesSimpleDto {

        @Schema(description = "날짜")
        private LocalDate tradeDate;

        @Schema(description = "시가")
        private BigDecimal openPrice;

        @Schema(description = "종가")
        private BigDecimal closePrice;

        @Schema(description = "고가")
        private BigDecimal highPrice;

        @Schema(description = "저가")
        private BigDecimal lowPrice;

        @Schema(description = "볼륨")
        private Long volume;

    }


}
