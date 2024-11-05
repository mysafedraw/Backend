package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.*;
import io.ssafy.p.k11a405.backend.pubsub.MessagePublisher;
import io.ssafy.p.k11a405.backend.pubsub.RoomMessageListener;
import io.ssafy.p.k11a405.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    /*
    @MessageMapping("/create-room")
    public void createRoom(ChatMessage message) {
        System.out.println("Received message: " + message); // 디버그용 로그
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        RoomMessageListener roomMessageListener = new RoomMessageListener(simpMessagingTemplate, genericJackson2JsonRedisSerializer);
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(roomMessageListener, defaultMethodName);
        // 메시지를 Redis Pub/Sub 채널로 발행
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new ChannelTopic("stomp-channel-topic"));
        messagePublisher.publish("stomp-message-channel", message);
    }
    */

    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO roomResponse = roomService.createRoom(roomRequestDTO.ownerId());
        return ResponseEntity.ok(roomResponse);
    }

    @PostMapping("/join")
    public ResponseEntity<List<String>> getAllUsersInRoom(@RequestBody RoomJoinRequestDTO roomJoinRequestDTO) {
        roomService.addUser(roomJoinRequestDTO.roomId(), roomJoinRequestDTO.userId());
        List<String> users = roomService.getAllUsersInRoom(roomJoinRequestDTO.roomId());
        return ResponseEntity.ok(users);
    }

    @MessageMapping("/{roomId}/join")
//    @SendTo("/topic/rooms/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, RoomJoinRequestDTO roomJoinRequestDTO) {
        roomService.joinRoom(roomId, roomJoinRequestDTO.userId());
    }
}
