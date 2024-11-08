package io.ssafy.p.k11a405.backend.dto.game;

public record VoteResponseDTO(
        Integer proCount,
        Integer conCount,
        GameAction action
) {}
