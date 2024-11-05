package io.ssafy.p.k11a405.backend.dto;

public record FindAssetValidationsRequestDTO (
        int stage,
        String assetName,
        int scenarioId
){}
