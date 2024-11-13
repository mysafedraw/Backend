package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.service.AnswerService;
import io.ssafy.p.k11a405.backend.service.GameService;
import io.ssafy.p.k11a405.backend.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final VoteService voteService;
    private final AnswerService answerService;

    @MessageMapping("/start")
    public void startGame(StartGameRequestDTO startGameRequestDTO) {
        String roomId = startGameRequestDTO.roomId();
        Integer stageNumber = startGameRequestDTO.stageNumber();
        Integer timeLimit = startGameRequestDTO.timeLimit();
        gameService.startGame(roomId, stageNumber, timeLimit);
    }

    @MessageMapping("/explanation-queue")
    public void addToExplanationQueue(ExplainRequestDTO explainRequestDTO) {
        gameService.addToExplanationQueue(explainRequestDTO);
    }

    @MessageMapping("/say")
    public void haveASay(HaveASayRequestDTO haveASayRequestDTO) {
        gameService.haveASay(haveASayRequestDTO.roomId(), haveASayRequestDTO.userId());
    }

    @MessageMapping("/answers")
    public void checkAllAnswers(CheckAllAnswersRequestDTO checkAllAnswersRequestDTO) {
        answerService.checkAllAnswers(checkAllAnswersRequestDTO.roomId());
    }

    @MessageMapping("/answer")
    public void checkAnswer(CheckAnswerRequestDTO checkAnswerRequestDTO) {
        String userId = checkAnswerRequestDTO.userId();
        String roomId = checkAnswerRequestDTO.roomId();
        Integer scenarioId = checkAnswerRequestDTO.scenarioId();
        String userAnswer = checkAnswerRequestDTO.answer();
        Integer stageNumber = checkAnswerRequestDTO.stageNumber();
        answerService.checkAnswer(roomId, userId, scenarioId, userAnswer, stageNumber);

    }

    @MessageMapping("/vote")
    public void vote(VoteRequestDTO voteRequestDTO) {
        voteService.vote(voteRequestDTO.roomId(), voteRequestDTO.isAgreed(), voteRequestDTO.userId());
    }

    @MessageMapping("/confirm")
    public void confirmAnswer(ConfirmRequestDTO confirmRequestDTO) {
        answerService.confirmAnswer(confirmRequestDTO.userId(), confirmRequestDTO.isConfirmed(), confirmRequestDTO.roomId());
    }

    @MessageMapping("/vote/end")
    public void endVote(EndVoteRequestDTO endVoteRequestDTO) {
        voteService.endVote(endVoteRequestDTO.roomId(), endVoteRequestDTO.userId());
    }

    @MessageMapping("/drawing/start")
    public void startDrawing(StartDrawingRequestDTO startDrawingRequestDTO) {
        gameService.startDrawing(startDrawingRequestDTO.roomId());
    }
}
