package io.ssafy.p.k11a405.backend.dto;

import lombok.Builder;

@Builder
public record FindScenariosResponseDTO(
   int id,
   String name,
   String description,
   String imgUrl
) {}
