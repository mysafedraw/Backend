package io.ssafy.p.k11a405.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // STOMP 브로커를 Redis를 통해 작동하도록 설정
//        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app", "/chat", "/games");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOrigins("http://127.0.0.1:5500") // 프론트엔드 서버 도메인 추가
                .withSockJS();
    }
}