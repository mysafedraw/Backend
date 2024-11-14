package io.ssafy.p.k11a405.backend.dto;

import lombok.Builder;

public record SendChatResponseDTO(
        String senderId,
        String roomId,
        String content,
        String nickname,
        String sentAt,
        String avatarsImgSrc,
        ChatAction action
) {

    @Builder
    public SendChatResponseDTO(SendChatRequestDTO sendChatRequestDTO, String sentAt, String avatarsImgSrc, ChatAction action) {
        this(sendChatRequestDTO.senderId(), sendChatRequestDTO.roomId(), sendChatRequestDTO.content(), sendChatRequestDTO.nickname(), sentAt, avatarsImgSrc, action);
    }
}
