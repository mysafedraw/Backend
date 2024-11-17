package io.ssafy.p.k11a405.backend.controller;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.ssafy.p.k11a405.backend.global.DataResponse;
import io.ssafy.p.k11a405.backend.service.AudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;

    @PostMapping("/sessions")
    public ResponseEntity<DataResponse<String>> initializeSession(@RequestBody Map<String, Object> params) throws OpenViduJavaClientException, OpenViduHttpException {
        String sessionId = audioService.initializeSession(params);
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.of("세션 생성에 성공했습니다.", sessionId));
    }

    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<DataResponse<String>> createToken(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        String token = audioService.createConnection(params, sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse.of("토큰 생성에 성공했습니다.", token));
    }
}
