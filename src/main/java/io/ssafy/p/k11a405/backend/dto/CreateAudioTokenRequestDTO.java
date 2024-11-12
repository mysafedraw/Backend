package io.ssafy.p.k11a405.backend.dto;

public record CreateAudioTokenRequestDTO(
        String roomId,
        String userId
) {}
