package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.dto.CheckAllAnswersRequestDTO;
import io.ssafy.p.k11a405.backend.dto.ExplainRequestDTO;
import io.ssafy.p.k11a405.backend.dto.HaveASayRequestDTO;
import io.ssafy.p.k11a405.backend.dto.StartGameRequestDTO;
import io.ssafy.p.k11a405.backend.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

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
}
