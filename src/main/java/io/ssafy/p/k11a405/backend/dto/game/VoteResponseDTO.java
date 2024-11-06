package io.ssafy.p.k11a405.backend.dto.game;

public record VoteResponseDTO(
        String userId,
        boolean isAgreed
) {}
