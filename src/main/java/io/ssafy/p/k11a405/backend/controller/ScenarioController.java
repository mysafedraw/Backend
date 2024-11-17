package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.FindScenariosResponseDTO;
import io.ssafy.p.k11a405.backend.global.DataResponse;
import io.ssafy.p.k11a405.backend.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //2.단계별 에셋 정답 조회
    @GetMapping(value="/correct")
    ResponseEntity<DataResponse<Boolean>> findAssetValidations(@RequestParam int stage,
                                                               @RequestParam String assetName,
                                                               @RequestParam int scenarioId) {
        Boolean isCorrect = scenarioService.findAssetValidations(stage, assetName, scenarioId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResponse.of("단계별 에셋 정답 조회에 성공했습니다. 정답: True, 오답: False", isCorrect));
    }
}
