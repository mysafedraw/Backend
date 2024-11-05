package io.ssafy.p.k11a405.backend.dto;

public record UpdateUserRequestDTO(
        String userId,
        String nickname,
        Integer avatarId
) {}
