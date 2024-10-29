package io.ssafy.p.k11a405.backend.controller;
import io.ssafy.p.k11a405.backend.dto.MessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public MessageDTO sendMessage(MessageDTO message) {
        String str = message.getContent();
        message.setContent(message.getContent() + str);
        return message;
    }
}