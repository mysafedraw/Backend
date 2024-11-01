package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.*;
import io.ssafy.p.k11a405.backend.pubsub.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final String redisKeyPrefix = "games:";

    private final MessagePublisher messagePublisher;
    private final StringRedisTemplate stringRedisTemplate;

    public void startGame(StartGameRequestDTO startGameRequestDTO) {
        String channelName = redisKeyPrefix + startGameRequestDTO.roomId();
        // db에서 scenarioDialog 가져오기

        String tmpScenarioDialog = "헉 불이났어요!!";
        StartGameResponseDTO startGameResponseDTO = new StartGameResponseDTO(tmpScenarioDialog, GameAction.GAME_START);
        messagePublisher.publish(channelName, startGameResponseDTO);
    }

    public void addToExplanationQueue(ExplainRequestDTO explainRequestDTO) {
        String channelName = redisKeyPrefix + explainRequestDTO.roomId();
        String enqueuedKey = redisKeyPrefix + explainRequestDTO.roomId() + ":enqueued";
        String queueKey = redisKeyPrefix + explainRequestDTO.roomId() + ":explanationQueue";

        // 추가된적 있는지 확인
        // key가 있는지
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(enqueuedKey))) {
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(enqueuedKey, explainRequestDTO.userId()))) {
                return;
            }
        }

        stringRedisTemplate.opsForSet().add(enqueuedKey, explainRequestDTO.userId());
        stringRedisTemplate.opsForList().rightPush(queueKey, explainRequestDTO.userId());
        List<String> userIds = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        ExplainResponseDTO explainResponseDTO = new ExplainResponseDTO(userIds, GameAction.ADD_EXPLAIN_QUEUE);
        messagePublisher.publish(channelName, explainResponseDTO);
    }
}
