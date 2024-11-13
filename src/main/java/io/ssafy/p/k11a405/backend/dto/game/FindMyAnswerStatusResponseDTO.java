package io.ssafy.p.k11a405.backend.dto.game;

public record FindMyAnswerStatusResponseDTO(
        String userId,
        String nickname,
        AnswerStatus isCorrect,
        String drawingSrc,
        String avatarsImgSrc,
        GameAction action
) {
    public FindMyAnswerStatusResponseDTO(AnswerStatusResponseDTO answerStatusResponseDTO, GameAction action) {
        this(answerStatusResponseDTO.userId(), answerStatusResponseDTO.nickname(), answerStatusResponseDTO.isCorrect(),
                answerStatusResponseDTO.drawingSrc(), answerStatusResponseDTO.avatarsImgSrc(), action);
    }
}
