package io.ssafy.p.k11a405.backend.dto.game;

public record InGameResponseDTO(
        String userId,
        boolean isCorrect,
        GameAction action
) {}
