package dongguk.osori.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                // 세션 설정
                .sessionManagement(session -> session
                        .sessionFixation().newSession() // 로그인 시 세션 생성
                        .maximumSessions(1) // 사용자당 최대 세션 수
                        .maxSessionsPreventsLogin(false) // 초과하면 이전 세션 만료
                )
                // 권한 설정
                .authorizeHttpRequests(authz -> authz
                        // TODO: 이후 권한 추가해서 수정 필요
                        .requestMatchers("/api/**").permitAll()
                        //.requestMatchers("/api/users/signup", "/api/users/login", "/api/users/logout").permitAll() // 회원가입, 로그인, 로그아웃은 인증 불필요
                        //.requestMatchers("/api/**").authenticated() // 그 외 /api/**는 인증 필요
                        .anyRequest().permitAll() // 그 외 모든 요청은 허용
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout") // 로그아웃 요청 경로
                        .logoutSuccessUrl("/api/users/login") // 로그아웃 성공 후 리다이렉트 URL
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                        .permitAll() // 로그아웃 요청은 인증 불필요
                );
        return http.build();
    }

    // 세션 무효화 처리
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정 빈 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://2024-2-oss-proj-osori-fe.vercel.app", "https://osori.store"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
