package vis.backend.demo.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/healthcheck", // Health Check
                                "/swagger-ui/**", // Swagger UI
                                "/v3/api-docs/**", // OpenAPI Docs

                                "/auth/login", // Login API
                                "/auth/register", // Register API
                                "/auth/logged-check"
                        ).permitAll() // 인증 없이 허용

                        // 나머지 요청은 인증만 필요 (모든 역할 허용)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
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