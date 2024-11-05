package io.ssafy.p.k11a405.backend.dto;

public record SendChatRequestDTO(
        String senderId,
        String roomId,
        String content,
        String nickname
) {}
