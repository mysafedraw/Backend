package io.ssafy.p.k11a405.backend.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record FindAvatarsResponseDTO (
        Integer id,
        String avatarName,
        String profileImg,
        List<String> hashTagNameList
) {}