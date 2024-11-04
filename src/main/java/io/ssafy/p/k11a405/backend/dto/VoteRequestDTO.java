package io.ssafy.p.k11a405.backend.dto;

public record VoteRequestDTO(
        String roomId,
        boolean isAgreed,
        String userId
) {}
