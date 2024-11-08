package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.common.RedisSubscriber;
import io.ssafy.p.k11a405.backend.dto.RoomAction;
import io.ssafy.p.k11a405.backend.dto.RoomEventMessage;
import io.ssafy.p.k11a405.backend.dto.RoomResponseDTO;
import io.ssafy.p.k11a405.backend.dto.SendChatResponseDTO;
import io.ssafy.p.k11a405.backend.dto.game.CheckAllAnswersResponseDTO;
import io.ssafy.p.k11a405.backend.dto.game.ExplainResponseDTO;
import io.ssafy.p.k11a405.backend.dto.game.StartGameResponseDTO;
import io.ssafy.p.k11a405.backend.dto.game.VoteResponseDTO;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        String channelName = "rooms:" + roomId;
        String destinationPath = "/rooms/" + roomId;

        // RedisSubscriber의 메서드로 구독 설정
        redisSubscriber.subscribeToChannel(channelName, RoomEventMessage.class, destinationPath);
    }

    public RoomResponseDTO createRoom(String ownerId) {
        Random random = new Random(System.currentTimeMillis());
        String roomId = String.valueOf(random.nextLong() % 1000000L);
        String roomKey = "rooms:" + roomId;

        // Redis에 방 정보 저장
        stringRedisTemplate.opsForHash().put(roomKey, "roomId", roomId);
        stringRedisTemplate.opsForHash().put(roomKey, "hostId", ownerId);

        subscribeChannelsOnRoom(roomId);

        // 방 정보에 방장 세션 ID 포함
//        String ownerSessionId = stringRedisTemplate.opsForHash().get("session:user", ownerId).toString();
//        stringRedisTemplate.opsForHash().put(roomKey, "ownerSessionId", ownerSessionId);

        return new RoomResponseDTO(roomId, ownerId, RoomAction.CREATE_ROOM);
    }

    public void joinRoom(String roomId, String userId) {
//        // Redis에 유저 입장 시간 기록
        addUser(roomId, userId);

        // 입장 메시지를 Redis 채널에 발행
        RoomEventMessage entryMessage = new RoomEventMessage(userId, roomId, RoomAction.ENTER_ROOM);
        String channelName = "rooms:" + roomId;
        redisSubscriber.subscribeToChannel(channelName, RoomEventMessage.class, "/rooms/" + roomId);
        genericMessagePublisher.publishString(channelName, entryMessage);
    }

    public void leaveRoom(String roomId, String userId) {

        // 퇴장 메시지를 Redis 채널에 발행
        RoomEventMessage entryMessage = new RoomEventMessage(userId, roomId, RoomAction.LEAVE_ROOM);
        leaveUser(roomId, userId);
        String channelName = "rooms:" + roomId;
        genericMessagePublisher.publishString(channelName, entryMessage);
    }

    public List<String> getAllUsersInRoom(String roomId) {
        String userKey = "rooms:" + roomId + ":users";
        // ZSetOperations 인스턴스를 가져와서 사용하는 방식
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        Set<TypedTuple<String>> users = zSetOperations.rangeWithScores(userKey, 0, -1);

        // users가 null이 아니면 id를 추출하고, null이면 빈 리스트를 반환
        return users != null
                ? users.stream().map(TypedTuple::getValue).collect(Collectors.toList())
                : List.of();
    }

    public void addUser(String roomId, String userId) {
        // Redis에 유저 입장 시간 기록
        String userKey = "rooms:" + roomId + ":users";
        long entryTime = System.currentTimeMillis() / 1000;
        stringRedisTemplate.opsForZSet().add(userKey, userId, entryTime);

        redisSubscriber.subscribeToChannel(userKey, VoteResponseDTO.class, "/games/" + userId);
    }

    public void leaveUser(String roomId, String userId) {
        String userKey = "rooms:" + roomId + ":users";
        stringRedisTemplate.opsForZSet().remove(userKey, userId);
    }

    public String getHostId(String roomId) {
        String key = "rooms:" + roomId;
        return String.valueOf(stringRedisTemplate.opsForHash().get(key, "hostId"));
    }

    private void subscribeChannelsOnRoom(String roomId) {
        String channelName = "rooms:" + roomId;
        String chatChannel = "chat:" + roomId;
        String gameStartChannel = "games:" + roomId + ":start";
        String gameExplainQueue = "games:" + roomId + ":explainQueue";
        String gameAllAnswersChannel = "games:" + roomId + ":allAnswers";
        redisSubscriber.subscribeToChannel(channelName, RoomEventMessage.class, "/rooms/" + roomId);
        redisSubscriber.subscribeToChannel(chatChannel, SendChatResponseDTO.class, "/chat/" + roomId);
        redisSubscriber.subscribeToChannel(gameStartChannel, StartGameResponseDTO.class, "/games/" + roomId);
        redisSubscriber.subscribeToChannel(gameExplainQueue, ExplainResponseDTO.class, "/games/" + roomId);
        redisSubscriber.subscribeToChannel(gameAllAnswersChannel, CheckAllAnswersResponseDTO.class, "/games/" + roomId);
    }
}