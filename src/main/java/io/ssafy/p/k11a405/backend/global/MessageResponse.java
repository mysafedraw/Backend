package io.ssafy.p.k11a405.backend.global;

import lombok.Getter;

@Getter
public class MessageResponse {
    private final String message;

    // 생성자: 메시지 전달
    private MessageResponse(String message) {
        this.message = message;
    }

    // 메시지를 설정하는 정적 팩토리 메서드
    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}

