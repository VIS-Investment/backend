package vis.backend.demo.stock.strategy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import vis.backend.demo.stock.dto.StockDto;
import vis.backend.demo.stock.dto.StockDto.StockPricesSimpleDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientFetcher {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://query1.finance.yahoo.com")
            .defaultHeader("User-Agent", "Mozilla/5.0")
            .build();

    public List<StockPricesSimpleDto> fetch(String ticker, String range) {
        try {
            Map<String, Object> body = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v8/finance/chart/{ticker}")
                            .queryParam("interval", "1d")
                            .queryParam("range", range)
                            .build(ticker))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if (body == null || !body.containsKey("chart")) {
                return List.of();
            }

            Object chartObj = body.get("chart");
            if (!(chartObj instanceof Map<?, ?> chartRaw)) {
                return List.of();
            }

            Map<String, Object> chart = castToMap(chartRaw);
            Object resultObj = chart.get("result");
            if (!(resultObj instanceof List<?> rawResults)) {
                return List.of();
            }
            if (rawResults.isEmpty()) {
                return List.of();
            }

            Map<String, Object> result = castToMap(rawResults.getFirst());
            List<Long> timestamps = castToLongList(result.get("timestamp"));

            Map<String, Object> indicators = castToMap(result.get("indicators"));
            Map<String, Object> quote = castToMap(((List<?>) indicators.get("quote")).getFirst());

            List<Double> opens = castToDoubleList(quote.get("open"));
            List<Double> closes = castToDoubleList(quote.get("close"));
            List<Double> highs = castToDoubleList(quote.get("high"));
            List<Double> lows = castToDoubleList(quote.get("low"));
            List<Long> volumes = castToLongList(quote.get("volume"));

            int minSize = Stream.of(opens.size(), closes.size(), highs.size(), lows.size(), volumes.size(),
                            timestamps.size())
                    .min(Integer::compareTo).orElse(0);

            List<StockDto.StockPricesSimpleDto> dtos = new ArrayList<>();
            for (int i = 0; i < minSize; i++) {
                if (opens.get(i) == null || closes.get(i) == null) {
                    continue;
                }
                LocalDate date = Instant.ofEpochSecond(timestamps.get(i))
                        .atZone(ZoneId.of("UTC")).toLocalDate();

                dtos.add(StockDto.StockPricesSimpleDto.builder()
                        .tradeDate(date)
                        .openPrice(BigDecimal.valueOf(opens.get(i)))
                        .closePrice(BigDecimal.valueOf(closes.get(i)))
                        .highPrice(BigDecimal.valueOf(highs.get(i)))
                        .lowPrice(BigDecimal.valueOf(lows.get(i)))
                        .volume(volumes.get(i))
                        .build());
                // log.info("[" + ticker + "] " + "[" + date + "] " + "is fetched");
            }

            return dtos;

        } catch (Exception e) {
            log.error("WebClient fetch error for {}: {}", ticker, e.getMessage());
            return List.of();
        }
    }

    private List<Double> castToDoubleList(Object obj) {
        if (!(obj instanceof List<?> rawList)) {
            return null;
        }
        List<Double> result = new ArrayList<>();
        for (Object o : rawList) {
            if (o instanceof Number) {
                result.add(((Number) o).doubleValue());
            } else {
                result.add(null);
            }
        }
        return result;
    }

    private List<Long> castToLongList(Object obj) {
        if (!(obj instanceof List<?> rawList)) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (Object o : rawList) {
            if (o instanceof Number) {
                result.add(((Number) o).longValue());
            } else {
                result.add(null);
            }
        }
        return result;
    }

    private Map<String, Object> castToMap(Object obj) {
        if (!(obj instanceof Map<?, ?> map)) {
            return Map.of();
        }
        return map.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        Map.Entry::getValue,
                        (a, b) -> b
                ));
    }
}