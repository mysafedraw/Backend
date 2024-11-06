package io.ssafy.p.k11a405.backend.dto.game;

public record CheckAnswerRequestDTO(
        String roomId,
        Integer scenarioId,
        String answer,
        String userId,
        Integer stageNumber
) {}
