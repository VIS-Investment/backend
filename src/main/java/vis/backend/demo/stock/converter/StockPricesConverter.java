package vis.backend.demo.stock.converter;

import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;
import vis.backend.demo.stock.domain.StockPricesId;
import vis.backend.demo.stock.dto.StockDto;

public class StockPricesConverter {
    public static StockPricesCompositeIdx toEntity(StockDto.StockPricesSimpleDto dto, StockInfo stockInfo) {
        return StockPricesCompositeIdx.builder()
                .id(new StockPricesId(stockInfo.getId(), dto.getTradeDate()))
                .stockInfo(stockInfo)
                .openPrice(dto.getOpenPrice())
                .closePrice(dto.getClosePrice())
                .highPrice(dto.getHighPrice())
                .lowPrice(dto.getLowPrice())
                .volume(dto.getVolume())
                .build();
    }
}
