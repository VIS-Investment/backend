package vis.backend.demo.stock.strategy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vis.backend.demo.stock.converter.StockPricesConverter;
import vis.backend.demo.stock.domain.StockInfo;
import vis.backend.demo.stock.domain.StockPricesCompositeIdx;

@Component("webclient")
@RequiredArgsConstructor
public class WebClientFetchStrategy implements FetchStrategy {

    private final WebClientFetcher fetcher;

    @Override
    public List<StockPricesCompositeIdx> fetch(List<StockInfo> infos, String range) {
        // 비동기 호출 → 병렬 실행
        List<CompletableFuture<List<StockPricesCompositeIdx>>> futures = infos.stream()
                .map(info -> CompletableFuture.supplyAsync(() ->
                        fetcher.fetch(info.getTicker(), range).stream()
                                .map(dto -> StockPricesConverter.toEntity(dto, info))
                                .collect(Collectors.toList())
                )).toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public String getType() {
        return "webclient";
    }
}