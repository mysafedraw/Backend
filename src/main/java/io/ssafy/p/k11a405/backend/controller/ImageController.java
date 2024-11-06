package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.game.SubmitDrawingRequestDTO;
import io.ssafy.p.k11a405.backend.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/images/answer")
    public ResponseEntity<Void> submitAnswerDrawing(@RequestParam("file") MultipartFile file,
                                                    SubmitDrawingRequestDTO submitDrawingRequestDTO) throws IOException {
        imageService.uploadAnswerDrawing(file, submitDrawingRequestDTO.userId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
