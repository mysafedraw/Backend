package io.ssafy.p.k11a405.backend.dto;

public record CheckAnswerResponseDTO(
        String userId,
        boolean isCorrect
) {}
