package io.ssafy.p.k11a405.backend.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.k11a405.backend.dto.ChatMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate template;

    private final GenericJackson2JsonRedisSerializer serializer; // JSON 직렬화기
    public RedisMessageSubscriber(SimpMessagingTemplate template) {
        this.template = template;
        this.serializer = new GenericJackson2JsonRedisSerializer();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신된 JSON 메시지를 ChatMessage 객체로 변환
            ChatMessage chatMessage = (ChatMessage) serializer.deserialize(message.getBody());
            System.out.println("Parsed ChatMessage: " + chatMessage);
            template.convertAndSend("/topic/messages", chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}