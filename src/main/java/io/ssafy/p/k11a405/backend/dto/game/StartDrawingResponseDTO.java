package io.ssafy.p.k11a405.backend.dto.game;

public record StartDrawingResponseDTO(
        Long endTime,
        Integer timeLimit,
        GameAction action
) {}
