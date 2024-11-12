package io.ssafy.p.k11a405.backend.dto;

public record CreateAudioTokenResponseDTO(
        String audioAccessToken,
        RoomAction action
) {
}
