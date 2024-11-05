package io.ssafy.p.k11a405.backend.dto;

import lombok.Builder;

public record SendChatResponseDTO(
        String senderId,
        String roomId,
        String content,
        String nickname,
        String sentAt
) {

    @Builder
    public SendChatResponseDTO(SendChatRequestDTO sendChatRequestDTO, String sentAt) {
        this(sendChatRequestDTO.senderId(), sendChatRequestDTO.roomId(), sendChatRequestDTO.content(), sendChatRequestDTO.nickname(), sentAt);
    }
}
