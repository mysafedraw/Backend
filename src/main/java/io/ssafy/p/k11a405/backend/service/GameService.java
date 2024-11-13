package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.ScenarioType;
import io.ssafy.p.k11a405.backend.dto.UserResponseDTO;
import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.entity.Dialogue;
import io.ssafy.p.k11a405.backend.entity.DialogueSituation;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final String redisKeyPrefix = "games:";
    private final String roomKeyPrefix = "rooms:";
    private final String isCorrectField = "isCorrect";
    private final String timeLimitField = "timeLimit";
    private final String drawingSrcField = "drawingSrc";

    private final GenericMessagePublisher genericMessagePublisher;
    private final StringRedisTemplate stringRedisTemplate;

    private final DialogueService dialogueService;
    private final AnswerService answerService;
    private final UserService userService;
    private final RoomService roomService;

    public void startGame(String roomId, Integer stageNumber, Integer timeLimit) {
        String channelName = redisKeyPrefix + roomId + ":start";
        // db에서 scenarioDialog 가져오기
        String situationTag = ScenarioType.SITUATION.getKoreanName();
        Dialogue dialogue = dialogueService.findScenarioByStageAndSituation(stageNumber, situationTag);
        DialogueSituation dialogueSituation = dialogue.getDialogues().get(0);
        clearRoomStatus(roomId);
        String roomKey = roomKeyPrefix + roomId;
        if (stageNumber == 1) {
            stringRedisTemplate.opsForHash().put(roomKey, timeLimitField, String.valueOf(timeLimit));
        }

        StartGameResponseDTO startGameResponseDTO = new StartGameResponseDTO(dialogueSituation.getSituationDialogue(), GameAction.GAME_START);
        genericMessagePublisher.publishString(channelName, startGameResponseDTO);
    }

    public void addToExplanationQueue(ExplainRequestDTO explainRequestDTO) {
        if (correctUser(explainRequestDTO.userId())) {
            return;
        }
        String channelName = redisKeyPrefix + explainRequestDTO.roomId() + ":explainQueue";
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
        List<AnswerStatusResponseDTO> answerStatuses = userIds.stream().map(answerService::getUserAnswerStatus).toList();
        ExplainResponseDTO explainResponseDTO = new ExplainResponseDTO(answerStatuses, GameAction.ADD_EXPLAIN_QUEUE);
        genericMessagePublisher.publishString(channelName, explainResponseDTO);
    }

    public void haveASay(String roomId, String userId) {
        String channelName = redisKeyPrefix + roomId + ":haveASay";
        UserResponseDTO userInfoByUserId = userService.getUserInfoByUserId(userId);
        String drawingSrc = String.valueOf(stringRedisTemplate.opsForHash().get("user:" + userId, drawingSrcField));
        HaveASayResponseDTO haveASayResponseDTO = HaveASayResponseDTO.builder()
                .userId(userId)
                .nickname(userInfoByUserId.nickname())
                .avatarsImgSrc(userInfoByUserId.avatarsImg())
                .drawingSrc(drawingSrc)
                .action(GameAction.HAVE_A_SAY)
                .build();
        genericMessagePublisher.publishString(channelName, haveASayResponseDTO);
    }

    public void revokeASay(String roomId, String userId) {
        String channelName = redisKeyPrefix + roomId + ":revokeASay";
        RevokeASayResponseDTO revokeASayResponseDTO = new RevokeASayResponseDTO(GameAction.REVOKE_A_SAY);
        genericMessagePublisher.publishString(channelName, revokeASayResponseDTO);
    }

    public void startDrawing(String roomId) {
        String channelName = "games:" + roomId + ":startDrawing";
        String roomKey = roomKeyPrefix + roomId;
        int timeLimit = Integer.parseInt(
                String.valueOf(stringRedisTemplate.opsForHash().get(roomKey, timeLimitField)));
        Long endTime = System.currentTimeMillis() + (1000L * timeLimit);

        StartDrawingResponseDTO startDrawingResponseDTO = new StartDrawingResponseDTO(endTime, timeLimit, GameAction.DRAWING_START);

        genericMessagePublisher.publishString(channelName, startDrawingResponseDTO);
    }

    private boolean correctUser(String userId) {
        String key = "user:" + userId;
        AnswerStatus isCorrect = AnswerStatus.valueOf(String.valueOf(stringRedisTemplate.opsForHash().get(key, isCorrectField)));
        return AnswerStatus.CORRECT_ANSWER.equals(isCorrect);
    }

    private void clearRoomStatus(String roomId) {
        String enqueuedKey = redisKeyPrefix + roomId + ":enqueued";
        String queueKey = redisKeyPrefix + roomId + ":explanationQueue";

        ArrayList<String> keys = new ArrayList<>();
        keys.add(enqueuedKey);
        keys.add(queueKey);
        stringRedisTemplate.delete(keys);
    }
}
