package io.ssafy.p.k11a405.backend.dto.game;

public record StartGameRequestDTO(
        String roomId,
        Integer stageNumber,
        Integer timeLimit
) {}
