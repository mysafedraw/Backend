package io.ssafy.p.k11a405.backend.dto.game;

import java.util.List;

public record HaveASayResponseDTO(
        String userId,
        List<String> waitingQueue,
        GameAction action
) {}
