package io.ssafy.p.k11a405.backend.dto;

import java.util.List;

public record EnterRoomResponseDTO(
        UserResponseDTO host,
        List<UserResponseDTO> currentPlayers,
        RoomAction action
) {}
