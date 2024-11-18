package io.ssafy.p.k11a405.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.k11a405.backend.common.RedisSubscriber;
import io.ssafy.p.k11a405.backend.dto.*;
import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessagePublisher;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessageSubscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final String gameKeyPrefix = "games:";
    private final String roomKeyPrefix = "rooms:";
    private final String userKeyPrefix = "user:";
    private final String chatKeyPrefix = "chat:";
    private final Map<String, List<AdapterInfo>> adapterInfos = new HashMap<>();

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisSubscriber redisSubscriber;  // RedisSubscriber 주입
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GenericMessagePublisher genericMessagePublisher;
    private final ObjectMapper objectMapper;

    private final UserService userService;

    public RoomResponseDTO createRoom(String ownerId) {
        Random random = new Random(System.currentTimeMillis());
        String roomId = String.valueOf(Math.abs(random.nextLong() % 1000000L));
        String roomKey = roomKeyPrefix + roomId;

        // Redis에 방 정보 저장
        stringRedisTemplate.opsForHash().put(roomKey, "roomId", roomId);
        stringRedisTemplate.opsForHash().put(roomKey, "hostId", ownerId);

        subscribeChannelsOnRoom(roomId, ownerId);

        return new RoomResponseDTO(roomId, ownerId, RoomAction.CREATE_ROOM);
    }

    public void joinRoom(String roomId, String userId) {
//        // Redis에 유저 입장 시간 기록
        addUser(roomId, userId);

        String hostId = getHostId(roomId);

        List<String> userIds = getAllUsersInRoom(roomId);
        List<UserResponseDTO> users = userIds.stream()
                .filter(id -> !hostId.equals(id))
                .map(userService::getUserInfoByUserId).toList();

        UserResponseDTO host = userService.getUserInfoByUserId(hostId);

        EnterRoomResponseDTO enterRoomResponseDTO = new EnterRoomResponseDTO(host, users, RoomAction.ENTER_ROOM);

        String channelName = roomKeyPrefix + roomId;
        // 입장 메시지를 Redis 채널에 발행
        genericMessagePublisher.publishString(channelName, enterRoomResponseDTO);
    }

    public void leaveRoom(String roomId, String userId) {

        // 퇴장 메시지를 Redis 채널에 발행
        RoomEventMessage entryMessage = new RoomEventMessage(userId, roomId, RoomAction.LEAVE_ROOM);
        leaveUser(roomId, userId);
        String channelName = roomKeyPrefix + roomId;
        genericMessagePublisher.publishString(channelName, entryMessage);
    }

    public List<String> getAllUsersInRoom(String roomId) {
        String userKey = roomKeyPrefix + roomId + ":users";
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
        String userKey = roomKeyPrefix + roomId + ":users";
        String personalKey = userKeyPrefix + userId;
        long entryTime = System.currentTimeMillis() / 1000;
        stringRedisTemplate.opsForZSet().add(userKey, userId, entryTime);
        stringRedisTemplate.opsForHash().put(personalKey, "score", "0");

        redisSubscriber.subscribeToChannel(personalKey, VoteResponseDTO.class, "/games/" + userId);
    }

    public void leaveUser(String roomId, String userId) {
        String userKey = roomKeyPrefix + roomId + ":users";
        stringRedisTemplate.opsForZSet().remove(userKey, userId);
    }

    public String getHostId(String roomId) {
        String key = roomKeyPrefix + roomId;
        return String.valueOf(stringRedisTemplate.opsForHash().get(key, "hostId"));
    }

    public CheckRoomExistenceResponseDTO isExistingRoom(String roomId) {
        String roomKey = roomKeyPrefix + roomId;
        Boolean isExisting = stringRedisTemplate.hasKey(roomKey);
        return new CheckRoomExistenceResponseDTO(isExisting);
    }

    public void destroyRoom(String roomId) {
        unsubscribeChannels(adapterInfos.get(roomId));
        adapterInfos.remove(roomId);
        destroyKeys(roomId);
    }

    private void subscribeChannelsOnRoom(String roomId, String hostId) {
        adapterInfos.put(roomId, new ArrayList<>());
        adapterInfos.get(roomId).add(new AdapterInfo(roomKeyPrefix + roomId, EnterRoomResponseDTO.class, "/rooms/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(chatKeyPrefix + roomId, SendChatResponseDTO.class, "/chat/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":start", StartGameResponseDTO.class, "/games/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":explainQueue", ExplainResponseDTO.class, "/games/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":allAnswers", CheckAllAnswersResponseDTO.class, "/games/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":voteEnded", EndVoteResponseDTO.class, "/games/" + hostId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":startDrawing", StartDrawingResponseDTO.class, "/games/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":haveASay", HaveASayResponseDTO.class, "/games/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":revokeASay", RevokeASayResponseDTO.class, "/games/" + roomId));
        adapterInfos.get(roomId).add(new AdapterInfo(gameKeyPrefix + roomId + ":finalRanks", FinalRankResponseDTO.class, "/games/" + roomId));
        subscribeChannels(adapterInfos.get(roomId));
    }

    private void subscribeChannels(List<AdapterInfo> adapterInfos) {
        for (AdapterInfo adapterInfo : adapterInfos) {
            redisSubscriber.subscribeToChannel(adapterInfo.messageListenerAdapter, adapterInfo.channel);
        }
    }

    private void unsubscribeChannels(List<AdapterInfo> adapterInfos) {
        adapterInfos.stream()
                .map(adapterInfo -> adapterInfo.messageListenerAdapter).forEach(redisSubscriber::unsubscribeFromChannel);
    }

    private void destroyKeys(String roomId) {
        stringRedisTemplate.delete(roomKeyPrefix + roomId);
        stringRedisTemplate.delete(roomKeyPrefix + roomId + ":users");
        stringRedisTemplate.delete(gameKeyPrefix + roomId + ":enqueued");
        stringRedisTemplate.delete(gameKeyPrefix + roomId + ":explanationQueue");
    }

    class AdapterInfo {
        MessageListenerAdapter messageListenerAdapter;
        String channel;
        String destinationPath;

        AdapterInfo(String channel, Class<?> messageType, String destinationPath) {
            this.channel = channel;
            this.destinationPath = destinationPath;
            this.messageListenerAdapter = new MessageListenerAdapter(
                    new GenericMessageSubscribe<>(simpMessagingTemplate, objectMapper, messageType, destinationPath), "onMessage");
        }
    }
}