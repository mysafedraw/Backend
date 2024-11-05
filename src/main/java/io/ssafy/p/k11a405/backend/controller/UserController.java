package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.UserRequestDTO;
import io.ssafy.p.k11a405.backend.dto.UserResponseDTO;
import io.ssafy.p.k11a405.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // POST 요청으로 유저 생성
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO.nickname());
        return ResponseEntity.ok(userResponseDTO);
    }
}