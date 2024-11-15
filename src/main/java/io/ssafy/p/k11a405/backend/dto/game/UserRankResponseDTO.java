package io.ssafy.p.k11a405.backend.dto.game;

import io.ssafy.p.k11a405.backend.dto.UserResponseDTO;

public record UserRankResponseDTO(
        String userId,
        String nickname,
        Integer score,
        String avatarsImgSrc,
        Integer rank
) {
    public UserRankResponseDTO(UserResponseDTO userResponseDTO, Integer rank) {
        this(userResponseDTO.userId(), userResponseDTO.nickname(),
                userResponseDTO.score(), userResponseDTO.avatarsImg(), rank);
    }
}
