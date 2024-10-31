package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.ChatMessage;
import io.ssafy.p.k11a405.backend.pubsub.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessagePublisher messagePublisher;

    @MessageMapping("/send") // 클라이언트가 WebSocket으로 보내는 메시지 경로
    public void sendMessage(ChatMessage message) {
        System.out.println("Received message: " + message); // 디버그용 로그
        // 메시지를 Redis Pub/Sub 채널로 발행
        messagePublisher.publish("stomp-message-channel", message);
    }
}