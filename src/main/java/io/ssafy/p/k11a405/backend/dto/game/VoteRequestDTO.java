package io.ssafy.p.k11a405.backend.dto.game;

public record VoteRequestDTO(
        String roomId,
        boolean isAgreed,
        String userId
) {}
