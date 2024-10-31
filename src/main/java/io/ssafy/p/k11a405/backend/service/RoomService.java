package io.ssafy.p.k11a405.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.k11a405.backend.dto.RoomEventMessage;
import io.ssafy.p.k11a405.backend.dto.RoomResponseDTO;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessageSubscribe;
import io.ssafy.p.k11a405.backend.pubsub.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    public void subscribeToRoomChannel(String roomId) {
        String channelName = "room:" + roomId;
        String destinationPath = "/topic/rooms/" + roomId;  // 동적으로 WebSocket 경로 설정
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(
                new GenericMessageSubscribe<>(simpMessagingTemplate, objectMapper, RoomEventMessage.class, destinationPath));
        redisMessageListenerContainer.addMessageListener(listenerAdapter, new PatternTopic(channelName));
    }

    public RoomResponseDTO createRoom(String ownerId) {
        String roomUUID = UUID.randomUUID().toString();
        String roomKey = "room:" + roomUUID;

        // Redis에 방 정보 저장
        stringRedisTemplate.opsForHash().put(roomKey, "roomId", roomUUID);
        stringRedisTemplate.opsForHash().put(roomKey, "ownerId", ownerId);

        // 방 생성 후 방장 입장 및 구독
        enterRoom(roomUUID, ownerId);

        return new RoomResponseDTO(roomUUID, ownerId);
    }

    public void enterRoom(String roomId, String userId) {
        // Redis에 유저 입장 시간 기록
        String userKey = "room:" + roomId + ":users";
        long entryTime = System.currentTimeMillis() / 1000;
        stringRedisTemplate.opsForZSet().add(userKey, userId, entryTime);

        // 구독 로직
        subscribeToRoomChannel(roomId);

        // WebSocket을 통해 방 입장 메시지 전송
        RoomEventMessage entryMessage = new RoomEventMessage(userId, roomId, "enter");
        simpMessagingTemplate.convertAndSend("/topic/rooms/" + roomId, entryMessage);
    }
}