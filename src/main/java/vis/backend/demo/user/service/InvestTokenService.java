package vis.backend.demo.user.service;

import java.util.Map;
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
import vis.backend.demo.user.converter.InvestTokenConverter;
import vis.backend.demo.user.domain.InvestToken;
import vis.backend.demo.user.domain.User;
import vis.backend.demo.user.repository.InvestTokenRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestTokenService {

    @Value("${korea_investment.app_key}")
    private String appKey;

    @Value("${korea_investment.secret_key}")
    private String appSecret;

    private final InvestTokenRepository investTokenRepository;
    private final RestTemplate restTemplate;


    public InvestToken getKoreaInvestmentToken(User user) {
        return investTokenRepository.findByUser(user)
                .orElseGet(() -> createInvestToken(user));
    }

    private InvestToken createInvestToken(User user) {
        String url = "https://openapivts.koreainvestment.com:29443/oauth2/tokenP";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("grant_type", "client_credentials");
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);

        // 요청 바디 설정 (POST 요청은 보통 필요하지 않지만 필요할 경우 추가)
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // API 호출
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
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

        InvestToken investToken = InvestTokenConverter.toInvestToken(user, accessToken, accessTokenExpired);
        investTokenRepository.save(investToken);

        return investToken;
    }
}

