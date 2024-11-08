package io.ssafy.p.k11a405.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//
//public record RoomEventMessage(
//        String userId,
//        String roomId,
//        String action  // "enter" or "leave"
//) {}
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomEventMessage{
    private String userId;
    private String roomId;
    private RoomAction action;
}