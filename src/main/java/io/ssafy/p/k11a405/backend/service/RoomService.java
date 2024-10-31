package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.RoomEventMessage;
import io.ssafy.p.k11a405.backend.dto.RoomResponseDTO;
import io.ssafy.p.k11a405.backend.common.RedisSubscriber;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisSubscriber redisSubscriber;  // RedisSubscriber 주입
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GenericMessagePublisher genericMessagePublisher;

    // 채팅방 구독 메서드
    public void subscribeToRoomChannel(String roomId) {
        String channelName = "room:" + roomId;
        String destinationPath = "/topic/rooms/" + roomId;

        // RedisSubscriber의 메서드로 구독 설정
        redisSubscriber.subscribeToChannel(channelName, RoomEventMessage.class, destinationPath);
        log.info("Subscribed to room channel: {}", channelName);
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

        // Redis 채널 구독 설정
        String channelName = "room:" + roomId;
        redisSubscriber.subscribeToChannel(channelName, RoomEventMessage.class, "/topic/rooms/" + roomId);
        log.info("Subscribed to room channel: {}", channelName);

        // 입장 메시지를 Redis 채널에 발행
        RoomEventMessage entryMessage = new RoomEventMessage(userId, roomId, "enter");
        genericMessagePublisher.publishString(channelName, entryMessage);
        log.info("Message sent to {}: {}", "/topic/rooms/" + roomId, entryMessage);
    }
}