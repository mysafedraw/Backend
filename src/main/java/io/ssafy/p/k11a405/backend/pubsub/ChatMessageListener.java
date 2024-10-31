package io.ssafy.p.k11a405.backend.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RequiredArgsConstructor
public class ChatMessageListener implements MessageListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer;

    @Override
    public void onMessage(Message message, byte[] pattern) {

    }
}
