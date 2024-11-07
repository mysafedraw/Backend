package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.ChatAction;
import io.ssafy.p.k11a405.backend.dto.SendChatRequestDTO;
import io.ssafy.p.k11a405.backend.dto.SendChatResponseDTO;
import io.ssafy.p.k11a405.backend.pubsub.MessagePublisher;
import io.ssafy.p.k11a405.backend.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessagePublisher messagePublisher;

    private final DateUtil dateUtil;

    public void sendMessage(SendChatRequestDTO sendChatRequestDTO) {
        String sentAt = dateUtil.getChatFormatTime();
        String channel = "chat:" + sendChatRequestDTO.roomId();
        SendChatResponseDTO sendChatResponseDTO = SendChatResponseDTO.builder()
                .sendChatRequestDTO(sendChatRequestDTO).sentAt(sentAt).action(ChatAction.CHAT_MESSAGE).build();
        messagePublisher.publish(channel, sendChatResponseDTO);
    }
}
