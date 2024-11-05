package io.ssafy.p.k11a405.backend.repository;

import io.ssafy.p.k11a405.backend.entity.AssetValidations;
import io.ssafy.p.k11a405.backend.entity.Scenarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetValidationRepository extends JpaRepository<AssetValidations, Integer> {

    AssetValidations findByScenarioIdAndStageAndAssetName(Scenarios scenarioId, Integer stage, String assetName);
}
