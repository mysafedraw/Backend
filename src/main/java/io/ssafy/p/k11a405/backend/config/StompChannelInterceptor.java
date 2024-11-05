package io.ssafy.p.k11a405.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) { // StompCommand 사용
            String sessionId = accessor.getSessionId();
            String roomId = accessor.getFirstNativeHeader("roomId");
            String isRoomCreation = accessor.getFirstNativeHeader("isRoomCreation");

            // 방 생성 시 세션 ID를 저장하는 로직
            if ("true".equals(isRoomCreation)) {
                stringRedisTemplate.opsForHash().put("room:" + roomId, "ownerSessionId", sessionId);
                System.out.println("Stored owner session ID for room " + roomId);
            }
        }
        return message;
    }
}