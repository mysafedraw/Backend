package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.CheckRoomExistenceResponseDTO;
import io.ssafy.p.k11a405.backend.dto.RoomJoinRequestDTO;
import io.ssafy.p.k11a405.backend.dto.RoomRequestDTO;
import io.ssafy.p.k11a405.backend.dto.RoomResponseDTO;
import io.ssafy.p.k11a405.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/api/rooms")
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO roomResponse = roomService.createRoom(roomRequestDTO.hostId());
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/api/rooms/{roomId}")
    public ResponseEntity<CheckRoomExistenceResponseDTO> isExistingRoom(@PathVariable String roomId) {
        CheckRoomExistenceResponseDTO checkRoomExistenceResponseDTO = roomService.isExistingRoom(roomId);
        return ResponseEntity.ok(checkRoomExistenceResponseDTO);
    }

    @PostMapping("/api/rooms/join")
    public ResponseEntity<List<String>> getAllUsersInRoom(@RequestBody RoomJoinRequestDTO roomJoinRequestDTO) {
//        roomService.addUser(roomJoinRequestDTO.roomId(), roomJoinRequestDTO.userId());
        List<String> users = roomService.getAllUsersInRoom(roomJoinRequestDTO.roomId());
        return ResponseEntity.ok(users);
    }

    @MessageMapping("/join")
    public void joinRoom(RoomJoinRequestDTO roomJoinRequestDTO) {
       roomService.joinRoom(roomJoinRequestDTO.roomId(), roomJoinRequestDTO.userId());
    }

    @MessageMapping("/leave")
    public void leaveRoom(RoomJoinRequestDTO roomJoinRequestDTO) {
        roomService.leaveRoom(roomJoinRequestDTO.roomId(), roomJoinRequestDTO.userId());
    }
}
