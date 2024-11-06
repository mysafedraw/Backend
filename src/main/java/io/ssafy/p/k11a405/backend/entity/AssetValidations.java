package io.ssafy.p.k11a405.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class AssetValidations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "scenario_id", referencedColumnName = "id")
    private Scenarios scenarioId;

    @Column(nullable = false)
    Integer stage;

    @Column(nullable = false)
    String assetName;

    @Column(nullable = false)
    Boolean isCorrect;
}
