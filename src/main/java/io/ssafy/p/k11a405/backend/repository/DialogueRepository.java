package io.ssafy.p.k11a405.backend.repository;

import io.ssafy.p.k11a405.backend.entity.Dialogue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DialogueRepository extends MongoRepository<Dialogue, ObjectId> {

    Optional<Dialogue> findByStageNumberAndSituationTag(Integer stageNumber, String situationTag);
}
