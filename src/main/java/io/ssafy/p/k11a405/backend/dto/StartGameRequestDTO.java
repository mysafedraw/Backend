package io.ssafy.p.k11a405.backend.dto;

public record StartGameRequestDTO(
        String roomId,
        Integer stageNumber,
        Integer timeLimit
) {}
