package io.ssafy.p.k11a405.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 엔드포인트에 CORS 적용
                        .allowedOriginPatterns("*") // 프론트엔드 서버 도메인
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//                        .allowCredentials(true); // 자격 증명 지원 활성화
            }
        };
    }
}
