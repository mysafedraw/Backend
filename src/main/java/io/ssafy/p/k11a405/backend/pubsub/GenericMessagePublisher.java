package io.ssafy.p.k11a405.backend.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate stringRedisTemplate;

    public <T> void publishObject(String channel, T message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message); // 제네릭 메시지 JSON 변환
            redisTemplate.convertAndSend(channel, jsonMessage);
            System.out.println("Published to Redis channel " + channel + ": " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void publishString(String channel, T message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message); // 제네릭 메시지 JSON 변환
            stringRedisTemplate.convertAndSend(channel, jsonMessage);
            System.out.println("Published to Redis channel " + channel + ": " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}