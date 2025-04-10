package vis.backend.demo.stock.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vis.backend.demo.stock.strategy.FetchStrategy;

@Component
@RequiredArgsConstructor
public class FetchStrategySelector {
    private final Map<String, FetchStrategy> strategyMap;

    // 이 부분 좀 더 고려해봐야 함
    public FetchStrategy select(String range) {
        return "1d".equalsIgnoreCase(range)
                ? strategyMap.get("virtual")
                : strategyMap.get("webclient");
    }
}
