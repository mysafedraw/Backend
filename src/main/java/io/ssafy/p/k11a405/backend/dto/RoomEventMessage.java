package io.ssafy.p.k11a405.backend.dto;

public record RoomEventMessage(
        String userId,
        String roomId,
        String action  // "enter" or "leave"
) {}