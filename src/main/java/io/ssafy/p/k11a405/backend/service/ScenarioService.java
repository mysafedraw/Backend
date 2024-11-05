package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.FindScenariosResponseDTO;
import io.ssafy.p.k11a405.backend.entity.Scenarios;
import io.ssafy.p.k11a405.backend.repository.ScenarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;

    @Transactional
    public List<FindScenariosResponseDTO> findScenarioList() {
        return scenarioRepository.findAll().stream()
                .map(scenario -> FindScenariosResponseDTO.builder()
                        .imgUrl(scenario.getImgUrl())
                        .name(scenario.getName())
                        .description(scenario.getDescription())
                        .id(scenario.getId())
                        .build()).toList();
    }


}
