package io.ssafy.p.k11a405.backend.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> void publish(String channel, T message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message); // 제네릭 메시지 JSON 변환
            redisTemplate.convertAndSend(channel, jsonMessage);
            System.out.println("Published to Redis channel " + channel + ": " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}