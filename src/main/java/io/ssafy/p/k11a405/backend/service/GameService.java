package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.GameAction;
import io.ssafy.p.k11a405.backend.dto.StartGameRequestDTO;
import io.ssafy.p.k11a405.backend.dto.StartGameResponseDTO;
import io.ssafy.p.k11a405.backend.pubsub.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final String redisKeyPrefix = "games:";

    private final MessagePublisher messagePublisher;

    public void startGame(StartGameRequestDTO startGameRequestDTO) {
        String channelName = redisKeyPrefix + startGameRequestDTO.roomId();
        // db에서 scenarioDialog 가져오기

        String tmpScenarioDialog = "헉 불이났어요!!";
        StartGameResponseDTO startGameResponseDTO = new StartGameResponseDTO(tmpScenarioDialog, GameAction.GAME_START);
        messagePublisher.publish(channelName, startGameResponseDTO);
    }
}
