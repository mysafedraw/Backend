package io.ssafy.p.k11a405.backend.dto.game;

public enum AnswerStatus {
    CORRECT_ANSWER, INCORRECT_ANSWER, PROHIBITED_ANSWER;

    public static AnswerStatus getAnswerStatus(Boolean isCorrect) {
        if (isCorrect) {
            return CORRECT_ANSWER;
        }
        return PROHIBITED_ANSWER;
    }
}
