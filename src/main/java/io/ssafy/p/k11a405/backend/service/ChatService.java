package io.ssafy.p.k11a405.backend.service;

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

    public void sendMessage(String requestDestination, SendChatRequestDTO sendChatRequestDTO) {
        String sentAt = dateUtil.getChatFormatTime();
        String channel = requestDestination + "/" + sendChatRequestDTO.roomId();
        SendChatResponseDTO sendChatResponseDTO = SendChatResponseDTO.builder()
                .sendChatRequestDTO(sendChatRequestDTO).sentAt(sentAt).build();
        messagePublisher.publish(channel, sendChatResponseDTO);
    }
}
