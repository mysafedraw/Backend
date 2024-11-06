package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.FindAssetValidationsRequestDTO;
import io.ssafy.p.k11a405.backend.dto.FindScenariosResponseDTO;
import io.ssafy.p.k11a405.backend.entity.AssetValidations;
import io.ssafy.p.k11a405.backend.entity.Scenarios;
import io.ssafy.p.k11a405.backend.exception.BusinessException;
import io.ssafy.p.k11a405.backend.exception.ErrorCode;
import io.ssafy.p.k11a405.backend.repository.AssetValidationRepository;
import io.ssafy.p.k11a405.backend.repository.ScenarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final AssetValidationRepository assetValidationRepository;

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

    @Transactional
    public Boolean findAssetValidations(int stage, String assetName, int scenarioId) {
        Scenarios scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCENARIO_NOT_FOUND));

        AssetValidations assetValidations = assetValidationRepository.findByScenarioIdAndStageAndAssetName(scenario, stage, assetName);

        if(assetValidations == null) throw new BusinessException(ErrorCode.ASSET_NOT_FOUND);
        return assetValidations.getIsCorrect();
    }
}
