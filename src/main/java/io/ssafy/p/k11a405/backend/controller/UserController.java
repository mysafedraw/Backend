package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.UpdateUserRequestDTO;
import io.ssafy.p.k11a405.backend.dto.UserRequestDTO;
import io.ssafy.p.k11a405.backend.dto.UserResponseDTO;
import io.ssafy.p.k11a405.backend.global.DataResponse;
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
        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO.nickname(), userRequestDTO.avatarId());
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping("/nickname")
    public ResponseEntity<DataResponse<UserResponseDTO>> updateNickname(@RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        UserResponseDTO userResponseDTO = userService.updateNickname(updateUserRequestDTO.nickname(), updateUserRequestDTO.userId());
        return ResponseEntity.ok(DataResponse.of("닉네임 변경에 성공했습니다.", userResponseDTO));
    }

    @PostMapping("/avatars")
    public ResponseEntity<DataResponse<UserResponseDTO>> updateAvatars(@RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        UserResponseDTO userResponseDTO = userService.updateAvatar(updateUserRequestDTO.avatarId(), updateUserRequestDTO.userId());
        return ResponseEntity.ok(DataResponse.of("아바타 변경에 성공했습니다.", userResponseDTO));

    }
}