package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.*;
import io.ssafy.p.k11a405.backend.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/start")
    public void startGame(StartGameRequestDTO startGameRequestDTO) {
        gameService.startGame(startGameRequestDTO);
    }

    @MessageMapping("/explanation-queue")
    public void addToExplanationQueue(ExplainRequestDTO explainRequestDTO) {
        gameService.addToExplanationQueue(explainRequestDTO);
    }

    @MessageMapping("/say")
    public void haveASay(HaveASayRequestDTO haveASayRequestDTO) {
        gameService.haveASay(haveASayRequestDTO);
    }

    @MessageMapping("/answers")
    public void checkAllAnswers(CheckAllAnswersRequestDTO checkAllAnswersRequestDTO) {
        gameService.checkAllAnswers(checkAllAnswersRequestDTO);
    }

    @MessageMapping("/answer")
    public void checkAnswer(CheckAnswerRequestDTO checkAnswerRequestDTO) {
        String userId = checkAnswerRequestDTO.userId();
        simpMessagingTemplate.convertAndSend("/games/" + userId, "response");
    }

    @MessageMapping("/vote")
    public void vote(VoteRequestDTO voteRequestDTO) {
        gameService.vote(voteRequestDTO.roomId(), voteRequestDTO.isAgreed(), voteRequestDTO.userId());
    }
}
