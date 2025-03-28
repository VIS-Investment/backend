package vis.backend.demo.stock.converter;

import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPrices;
import vis.backend.demo.stock.dto.StockDto;

public class StockPricesConverter {
    public StockPrices toEntity(StockDto.StockPricesSimpleDto dto, StockInfo stockInfo) {
        return StockPrices.builder()
                .stockInfo(stockInfo)
                .tradeDate(dto.getTradeDate())
                .openPrice(dto.getOpenPrice())
                .closePrice(dto.getClosePrice())
                .highPrice(dto.getHighPrice())
                .lowPrice(dto.getLowPrice())
                .volume(dto.getVolume())
                .build();
    }
}
