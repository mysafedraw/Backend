package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.entity.Dialogue;
import io.ssafy.p.k11a405.backend.repository.DialogueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DialogueService {

    private final DialogueRepository dialogRepository;

    public Dialogue findScenarioByStageAndSituation(Integer stageNumber, String situation) {
        return dialogRepository.findByStageNumberAndSituationTag(stageNumber, situation).orElseThrow();
    }
}
