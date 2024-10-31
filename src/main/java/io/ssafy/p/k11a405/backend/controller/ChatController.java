package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.SendChatRequestDTO;
import io.ssafy.p.k11a405.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/send") // 클라이언트가 WebSocket으로 보내는 메시지 경로
    public void sendMessage(@Header("simpDestination") String requestDestination, SendChatRequestDTO sendChatRequestDTO) {
        chatService.sendMessage(requestDestination, sendChatRequestDTO);
    }
}