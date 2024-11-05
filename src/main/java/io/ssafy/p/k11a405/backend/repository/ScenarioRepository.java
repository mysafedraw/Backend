package io.ssafy.p.k11a405.backend.repository;

import io.ssafy.p.k11a405.backend.entity.Scenario;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ScenarioRepository extends MongoRepository<Scenario, ObjectId> {

    Optional<Scenario> findByStageNumberAndSituationTag(Integer stageNumber, String situationTag);
}
