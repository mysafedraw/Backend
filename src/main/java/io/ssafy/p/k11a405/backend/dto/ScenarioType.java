package io.ssafy.p.k11a405.backend.dto;

import lombok.Getter;

@Getter
public enum ScenarioType {
    SITUATION("상황"), INTERACTION("상호작용");

    String koreanName;

    ScenarioType(String korValue) {
        this.koreanName = korValue;
    }
}
