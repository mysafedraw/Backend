package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.FindAvatarsResponseDTO;
import io.ssafy.p.k11a405.backend.dto.FindScenariosResponseDTO;
import io.ssafy.p.k11a405.backend.global.DataResponse;
import io.ssafy.p.k11a405.backend.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
@CrossOrigin
public class ScenarioController {

    private final ScenarioService scenarioService;

    //1. 시나리오 정보 조회
    @GetMapping
    ResponseEntity<DataResponse<List<FindScenariosResponseDTO>>> findScenarioList() {
        List<FindScenariosResponseDTO> findScenariosResponseDTOList = scenarioService.findScenarioList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResponse.of("시나리오 정보 조회에 성공했습니다.", findScenariosResponseDTOList));
    }
}
