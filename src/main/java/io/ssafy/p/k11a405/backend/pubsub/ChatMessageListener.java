package io.ssafy.p.k11a405.backend.pubsub;

import io.ssafy.p.k11a405.backend.dto.SendChatResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RequiredArgsConstructor
public class ChatMessageListener implements MessageListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer;
    private final StringRedisSerializer stringRedisSerializer;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        SendChatResponseDTO sendChatResponseDTO = (SendChatResponseDTO) genericJackson2JsonRedisSerializer.deserialize(message.getBody());
        String channel = stringRedisSerializer.deserialize(message.getChannel());

        simpMessagingTemplate.convertAndSend(channel, sendChatResponseDTO);
    }
}
