package io.ssafy.p.k11a405.backend.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "dialogue")
@Getter
public class Scenario {

    @Id
    private ObjectId id;
    private Integer stageNumber;
    private String description;
    private String situationTag;
    private String situationAsset;
    private List<Dialogue> dialogues;
}
