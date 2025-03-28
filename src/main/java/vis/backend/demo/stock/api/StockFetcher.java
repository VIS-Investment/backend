package vis.backend.demo.stock.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vis.backend.demo.stock.dto.StockDto;

@Component
public class StockFetcher {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<StockDto.StockPricesSimpleDto> fetch(String ticker, String range) {
        String url = String.format(
                "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=%s",
                ticker, range
        );

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    org.springframework.http.RequestEntity.get(url).build(),
                    new ParameterizedTypeReference<>() {
                    }
            );

            Map<String, Object> body = response.getBody();
            if (!body.containsKey("chart")) {
                return List.of();
            }

            Object chartObj = body.get("chart");
            if (!(chartObj instanceof Map)) {
                return List.of();
            }
            Map<String, Object> chart = castToMap(chartObj);
            Object resultObj = chart.get("result");
            if (!(resultObj instanceof List<?> rawResults)) {
                return List.of();
            }
            if (rawResults.isEmpty() || !(rawResults.getFirst() instanceof Map)) {
                return List.of();
            }
            List<Map<String, Object>> results = new ArrayList<>();
            for (Object r : rawResults) {
                results.add(castToMap(r));
            }

            Map<String, Object> result = results.getFirst();
            Object timestampObj = result.get("timestamp");
            if (!(timestampObj instanceof List<?> rawTimestamps)) {
                return List.of();
            }
            List<Long> timestamps = new ArrayList<>();
            for (Object ts : rawTimestamps) {
                if (ts instanceof Number) {
                    timestamps.add(((Number) ts).longValue());
                } else {
                    return List.of();
                }
            }

            Object indicatorsObj = result.get("indicators");
            if (!(indicatorsObj instanceof Map)) {
                return List.of();
            }
            Map<String, Object> indicators = castToMap(indicatorsObj);

            Object quoteListObj = indicators.get("quote");
            if (!(quoteListObj instanceof List<?> rawQuoteList)) {
                return List.of();
            }
            if (rawQuoteList.isEmpty() || !(rawQuoteList.getFirst() instanceof Map)) {
                return List.of();
            }
            Map<String, Object> quote = castToMap(rawQuoteList.getFirst());
            List<Double> opens = castToDoubleList(quote.get("open"));
            List<Double> closes = castToDoubleList(quote.get("close"));
            List<Double> highs = castToDoubleList(quote.get("high"));
            List<Double> lows = castToDoubleList(quote.get("low"));
            List<Long> volumes = castToLongList(quote.get("volume"));

            if (opens == null || closes == null || highs == null || lows == null || volumes == null) {
                return List.of();
            }

            List<StockDto.StockPricesSimpleDto> dtos = new ArrayList<>();

            for (int i = 0; i < timestamps.size(); i++) {
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
            }

            return dtos;
        } catch (Exception e) {
            System.out.println("Error fetching " + ticker + ": " + e.getMessage());
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
                .collect(java.util.stream.Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));
    }
}
