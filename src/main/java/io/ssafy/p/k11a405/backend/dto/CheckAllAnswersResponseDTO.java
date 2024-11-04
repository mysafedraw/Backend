package io.ssafy.p.k11a405.backend.dto;

import java.util.List;

public record CheckAllAnswersResponseDTO(
        List<AnswerStatusResponseDTO> users,
        GameAction gameAction
) {}
