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
import vis.backend.demo.user.repository.UserRepository;

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
    private final UserRepository userRepository;


    public InvestToken getKoreaInvestmentToken(User user) {
        return investTokenRepository.findByUser(user)
                .orElseGet(() -> createInvestToken(user));
    }

    private InvestToken createInvestToken(User user) {
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

        // InvestToken 생성 및 저장
        InvestToken investToken = InvestTokenConverter.toInvestToken(user, accessToken, accessTokenExpired);
        investTokenRepository.save(investToken);

        // 사용자와 연관 관계 설정
        user.setInvestToken(investToken);
        userRepository.save(user);

        return investToken;
    }
}

