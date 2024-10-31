package io.ssafy.p.k11a405.backend.pubsub;

import io.ssafy.p.k11a405.backend.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagePublisher {

    private final StringRedisTemplate redisTemplate;

    public void publish(String channel, Object message) {
        try {
            System.out.println("Publishing to Redis channel " + channel + ": " + message); // 디버깅 로그
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}