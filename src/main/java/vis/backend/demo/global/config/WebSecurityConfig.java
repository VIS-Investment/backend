package vis.backend.demo.global.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Arrays.asList(
                                "http://localhost:8080",
                                "http://localhost:3000" // 추후 배포 환경 추가
                        ));
                        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowCredentials(true); // 인정 정보(쿠키) 허용
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L); // 1시간
                        return config;
                    }
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/healthcheck", // Health Check
                                "/swagger-ui/**", // Swagger UI
                                "/v3/api-docs/**", // OpenAPI Docs

                                "/auth/login", // Login API
                                "/auth/register", // Register API
                                "/auth/logged-check"
                        ).permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // REST API 로그아웃
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200); // 로그아웃 성공 시 200 상태 반환
                        })
                );

        return http.build();
    }
}