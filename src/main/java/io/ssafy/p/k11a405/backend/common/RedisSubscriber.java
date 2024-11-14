package io.ssafy.p.k11a405.backend.common;

import io.ssafy.p.k11a405.backend.pubsub.GenericMessageSubscribe;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber {

    private final RedisMessageListenerContainer listenerContainer;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    // 공통 구독 메서드
    public <T> void subscribeToChannel(String channelName, Class<T> messageType, String destinationPath) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(
                new GenericMessageSubscribe<>(simpMessagingTemplate, objectMapper, messageType, destinationPath), "onMessage");
        listenerContainer.addMessageListener(listenerAdapter, new PatternTopic(channelName));
    }

    // 구독 해제 메서드도 필요 시 추가
    public void unsubscribeFromChannel(MessageListenerAdapter listenerAdapter) {
        listenerContainer.removeMessageListener(listenerAdapter);
    }

    public void subscribeToChannel(MessageListenerAdapter messageListenerAdapter, String channel) {
        listenerContainer.addMessageListener(messageListenerAdapter, new PatternTopic(channel));
    }
}