package io.ssafy.p.k11a405.backend.global;

import lombok.Getter;

@Getter
public class DataResponse<T> {
    // 응답 메시지
    private final String message;

    // 응답 데이터
    private final T data;

    // 생성자: 메시지와 데이터만 관리
    private DataResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // 정적 팩토리 메서드: 메시지와 데이터를 받아 DataResponse 객체 생성
    public static <T> DataResponse<T> of(String message, T data) {
        return new DataResponse<>(message, data);
    }

    // 빌더 패턴을 위한 정적 내부 클래스
    public static class Builder<T> {
        private String message;
        private T data;

        // 빌더 메서드: 메시지 설정
        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        // 빌더 메서드: 데이터 설정
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        // 빌드 메서드: DataResponse 객체 생성
        public DataResponse<T> build() {
            return new DataResponse<>(message, data);
        }
    }

    // 빌더 패턴의 진입점
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}

