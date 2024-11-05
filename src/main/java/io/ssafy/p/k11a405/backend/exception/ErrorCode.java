package io.ssafy.p.k11a405.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    AVATAR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 캐릭터를 찾을 수 없습니다."),
    SCENARIO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 시나리오를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
