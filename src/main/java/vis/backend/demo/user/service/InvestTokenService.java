package vis.backend.demo.user.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vis.backend.demo.global.api_payload.ErrorCode;
import vis.backend.demo.global.exception.GeneralException;
import vis.backend.demo.user.converter.InvestTokenConverter;
import vis.backend.demo.user.domain.InvestToken;
import vis.backend.demo.user.domain.User;
import vis.backend.demo.user.repository.InvestTokenRepository;
import vis.backend.demo.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestTokenService {

    @Value("${korea_investment.app_key}")
    private String appKey;

    @Value("${korea_investment.secret_key}")
    private String appSecret;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private final InvestTokenRepository investTokenRepository;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Transactional
    public void checkKoreaInvestmentToken(User user) {
        InvestToken investToken = investTokenRepository.findByUser(user)
                .orElseGet(() -> createInvestToken(user, "new"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = LocalDateTime.parse(investToken.getExpireTime(), formatter);
        if (expireTime.isBefore(now)) {
            createInvestToken(user, "refresh");
        }
    }

    private InvestToken createInvestToken(User user, String status) {
        String url = "https://openapivts.koreainvestment.com:29443/oauth2/tokenP";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        // 요청 본문 설정
        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", appKey,
                "appsecret", appSecret
        );

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // API 호출
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        // 응답 처리
        Map<String, String> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new RuntimeException("Failed to retrieve access token from API.");
        }

        // Access Token과 만료 시간 추출
        String accessToken = responseBody.get("access_token");
        String accessTokenExpired = responseBody.get("access_token_token_expired");

        InvestToken investToken;
        if (Objects.equals(status, "new")) {
            investToken = saveNewInvestToken(user, accessToken, accessTokenExpired);
        } else {
            investToken = saveRefreshInvestToken(user, accessToken, accessTokenExpired);
        }

        return investToken;
    }

    private InvestToken saveNewInvestToken(User user, String accessToken, String accessTokenExpired) {
        InvestToken investToken = InvestTokenConverter.toInvestToken(user, accessToken, accessTokenExpired);
        investTokenRepository.save(investToken);

        user.setInvestToken(investToken);
        userRepository.save(user);

        return investToken;
    }

    private InvestToken saveRefreshInvestToken(User user, String accessToken, String accessTokenExpired) {
        InvestToken investToken = investTokenRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));

        investToken.updateToken(accessToken, accessTokenExpired);
        investTokenRepository.save(investToken);

        return investToken;
    }
}

