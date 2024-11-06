package io.ssafy.p.k11a405.backend.dto.game;

public record CheckAnswerResponseDTO(
        String userId,
        boolean isCorrect
) {}
