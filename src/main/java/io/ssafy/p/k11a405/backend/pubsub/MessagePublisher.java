package io.ssafy.p.k11a405.backend.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.k11a405.backend.dto.ChatMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public MessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publish(String channel, ChatMessage message) {
        try {
            System.out.println("Publishing to Redis channel " + channel + ": " + message); // 디버깅 로그
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}