package vis.backend.demo.stock.service;

import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vis.backend.demo.stock.strategy.FetchStrategy;

@Component
@RequiredArgsConstructor
public class FetchStrategySelector {
    private final Map<String, FetchStrategy> strategyMap;

    public FetchStrategy select(String range) {
        if (Objects.equals(range, "1d")) {
            return strategyMap.get("virtual");
        } else {
            return strategyMap.get("batch");
        }
    }
}
