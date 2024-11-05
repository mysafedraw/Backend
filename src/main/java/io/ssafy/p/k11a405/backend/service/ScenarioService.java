package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.entity.Scenario;
import io.ssafy.p.k11a405.backend.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository dialogRepository;

    public Scenario findScenarioByStageAndSituation(Integer stageNumber, String situation) {
        return dialogRepository.findByStageNumberAndSituationTag(stageNumber, situation).orElseThrow();
    }
}
