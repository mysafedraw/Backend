package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.CreateAudioTokenRequestDTO;
import io.ssafy.p.k11a405.backend.dto.CreateAudioTokenResponseDTO;
import io.ssafy.p.k11a405.backend.service.AudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;

    @PostMapping("/token")
    public ResponseEntity<CreateAudioTokenResponseDTO> createToken(CreateAudioTokenRequestDTO createAudioTokenRequestDTO) {
        CreateAudioTokenResponseDTO createAudioTokenResponseDTO =
                audioService.createToken(createAudioTokenRequestDTO.userId(), createAudioTokenRequestDTO.roomId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createAudioTokenResponseDTO);
    }
}
