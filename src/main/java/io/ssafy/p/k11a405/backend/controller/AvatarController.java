package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.FindAvatarsInfoResponseDTO;
import io.ssafy.p.k11a405.backend.dto.FindAvatarsResponseDTO;
import io.ssafy.p.k11a405.backend.global.DataResponse;
import io.ssafy.p.k11a405.backend.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
@CrossOrigin
public class AvatarController {

    private final AvatarService avatarService;

    //1. 캐릭터 리스트 조회
    @GetMapping(value = "/list")
    ResponseEntity<DataResponse<List<FindAvatarsResponseDTO>>> findAvatarList() {
        List<FindAvatarsResponseDTO> findAvatarsResponseDTOList = avatarService.findAvatarList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResponse.of("캐릭터 리스트 조회에 성공했습니다.", findAvatarsResponseDTOList));
    }

    //2. 캐릭터 상세조회
    @GetMapping(value = "/{avatarsId}")
    ResponseEntity<DataResponse<FindAvatarsInfoResponseDTO>> findAvatarInfo(@PathVariable Integer avatarsId) {
        FindAvatarsInfoResponseDTO findAvatarsInfoResponseDTO = avatarService.findAvatarInfo(avatarsId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResponse.of("캐릭터 상세조회에 성공했습니다.", findAvatarsInfoResponseDTO));
    }
}
