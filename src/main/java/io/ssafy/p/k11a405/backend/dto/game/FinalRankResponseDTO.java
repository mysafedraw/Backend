package io.ssafy.p.k11a405.backend.dto.game;

import java.util.List;

public record FinalRankResponseDTO(
        List<UserRankResponseDTO> users,
        GameAction action
) {}
