package io.ssafy.p.k11a405.backend.dto;

public record ConfirmRequestDTO(
        String userId,
        boolean isConfirmed,
        String roomId
) {}
