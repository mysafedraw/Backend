package io.ssafy.p.k11a405.backend.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RequiredArgsConstructor
public class GenericMessageSubscribe<T> implements MessageListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final Class<T> messageType;
    private final String destinationPath;  // 동적으로 설정할 WebSocket 경로

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신한 메시지를 제네릭 타입으로 변환
            T deserializedMessage = objectMapper.readValue(message.getBody(), messageType);
            simpMessagingTemplate.convertAndSend(destinationPath, deserializedMessage);  // 지정된 경로로 메시지 전송
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}