package vis.backend.demo.stock.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vis.backend.demo.stock.strategy.FetchStrategy;

@Component
@RequiredArgsConstructor
public class FetchStrategySelector {
    private final Map<String, FetchStrategy> strategyMap;

    public FetchStrategy select(String range) {
        return strategyMap.get("virtual");
    }
}
