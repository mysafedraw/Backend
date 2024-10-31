package io.ssafy.p.k11a405.backend.dto;

import lombok.Builder;

@Builder
public record FindAvatarsInfoResponseDTO(
        String feature,
        String name,
        String assetImg
) {}
