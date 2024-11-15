package io.ssafy.p.k11a405.backend.dto;

public record UserResponseDTO(
        String userId,
        String nickname,
        String avatarsImg,
        Integer score
) {}

