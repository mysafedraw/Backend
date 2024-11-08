package io.ssafy.p.k11a405.backend.dto.game;

import lombok.Builder;

@Builder
public record EndVoteResponseDTO(
        String userId,
        String drawingSrc,
        Boolean isPassed,
        GameAction action
) {}
