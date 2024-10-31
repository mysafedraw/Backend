package io.ssafy.p.k11a405.backend.dto;

import lombok.Builder;

@Builder
public record SendChatResponseDTO(
        String senderId,
        String roomId,
        String content,
        String nickname,
        String sentAt
) {
}
