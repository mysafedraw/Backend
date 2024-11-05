package io.ssafy.p.k11a405.backend.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String ownerId = accessor.getFirstNativeHeader("ownerId"); // 클라이언트에서 보낸 ownerId 가져오기
        String sessionId = accessor.getSessionId();

        if (ownerId != null && sessionId != null) {
            stringRedisTemplate.opsForHash().put("session:user", ownerId, sessionId);
            System.out.println("Stored session ID for owner " + ownerId + " as " + sessionId);
        } else {
            System.out.println("Owner ID or session ID is missing");
        }
    }
}