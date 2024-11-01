package io.ssafy.p.k11a405.backend.dto;

import java.util.List;

public record ExplainResponseDTO(
        List<String> userIds,
        GameAction action
) {}
