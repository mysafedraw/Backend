package io.ssafy.p.k11a405.backend.dto.game;

import lombok.Builder;

@Builder
public record HaveASayResponseDTO(
        String userId,
        String nickname,
        String avatarsImgSrc,
        String drawingSrc,
        GameAction action
) {}
