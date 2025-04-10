package vis.backend.demo.stock.strategy;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vis.backend.demo.stock.dto.StockDto;

@Slf4j
@Component
public class VirtualThreadFetcher {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<StockDto.StockPricesSimpleDto> fetch(String ticker, String range) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://query1.finance.yahoo.com/v8/finance/chart/" + ticker.trim())
                .queryParam("interval", "1d")
                .queryParam("range", range)
                .build()
                .toUri();
        System.out.println(uri);

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, uri);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    request,
                    new ParameterizedTypeReference<>() {
                    }
            );

            Map<String, Object> body = response.getBody();
            if (!body.containsKey("chart")) {
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
            Object timestampObj = result.get("timestamp");
            if (!(timestampObj instanceof List<?> rawTimestamps)) {
                return List.of();
            }
            List<Long> timestamps = new ArrayList<>();
            for (Object ts : rawTimestamps) {
                if (ts instanceof Number num) {
                    timestamps.add(num.longValue());
                } else {
                    return List.of();
                }
            }

            Object indicatorsObj = result.get("indicators");
            if (!(indicatorsObj instanceof Map<?, ?> indicatorsRaw)) {
                return List.of();
            }
            Map<String, Object> indicators = castToMap(indicatorsRaw);

            Object quoteListObj = indicators.get("quote");
            if (!(quoteListObj instanceof List<?> quoteListRaw)) {
                return List.of();
            }
            if (quoteListRaw.isEmpty() || !(quoteListRaw.getFirst() instanceof Map)) {
                return List.of();
            }
            Map<String, Object> quote = castToMap(quoteListRaw.getFirst());
            List<Double> opens = castToDoubleList(quote.get("open"));
            List<Double> closes = castToDoubleList(quote.get("close"));
            List<Double> highs = castToDoubleList(quote.get("high"));
            List<Double> lows = castToDoubleList(quote.get("low"));
            List<Long> volumes = castToLongList(quote.get("volume"));

            if (opens == null || closes == null || highs == null || lows == null || volumes == null) {
                System.out.println("[" + ticker + "] One of the price lists is null");
                return List.of();
            }

            int minSize = Stream.of(opens.size(), closes.size(), highs.size(), lows.size(), volumes.size(),
                    timestamps.size()).min(Integer::compare).orElse(0);

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
            if (dtos.isEmpty()) {
                log.error("[" + ticker + "] No data added to DTOs (all nulls or empty)");
            }
            return dtos;

        } catch (Exception e) {
            log.error("[" + ticker + "] Exception: " + e.getMessage());
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