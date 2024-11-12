package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.exception.BusinessException;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final int answerScore = 100;
    private final String redisKeyPrefix = "games:";
    private final String nicknameField = "nickname";
    private final String drawSrcField = "drawingSrc";
    private final String avatarProfileImgField = "avatarProfileImg";
    private final String isCorrectField = "isCorrect";

    private final StringRedisTemplate stringRedisTemplate;
    private final GenericMessagePublisher genericMessagePublisher;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final RoomService roomService;
    private final ScenarioService scenarioService;

    public void checkAnswer(String roomId, String userId, Integer scenarioId, String userAnswer, Integer stageNumber) {
        // 정답 체크
        AnswerStatus answerStatus;
        try {
            Boolean isCorrect = scenarioService.findAssetValidations(stageNumber, userAnswer, scenarioId);
            answerStatus = AnswerStatus.getAnswerStatus(isCorrect);
            if (isCorrect) {
                addScore(userId);
            }
        } catch (BusinessException e) {
            answerStatus = AnswerStatus.INCORRECT_ANSWER;
        }

        // 유저 정답 여부 redis 저장
        String userKey = "user:" + userId;
        stringRedisTemplate.opsForHash().put(userKey, isCorrectField, String.valueOf(answerStatus));

        InGameResponseDTO inGameResponseDTO = InGameResponseDTO.builder()
                .userId(userId)
                .isCorrect(answerStatus)
                .action(GameAction.CHECK_ANSWER)
                .build();
        // 해당 유저에게 다이렉트로 정답 여부 전송
        simpMessagingTemplate.convertAndSend("/games/" + userId, inGameResponseDTO);
    }

    public void checkAllAnswers(String roomId) {
        String userKey = "rooms:" + roomId + ":users";
        Set<String> userIds = stringRedisTemplate.opsForZSet().range(userKey, 0, -1);
        String hostId = roomService.getHostId(roomId);
        List<AnswerStatusResponseDTO> answerStatuses = userIds.stream()
                .filter(Predicate.not(hostId::equals)).map(this::getUserAnswerStatus).toList();

        CheckAllAnswersResponseDTO checkAllAnswersResponseDTO = new CheckAllAnswersResponseDTO(answerStatuses, GameAction.CHECK_ALL_ANSWERS);
        String channelName = redisKeyPrefix + roomId + ":allAnswers";
        genericMessagePublisher.publishString(channelName, checkAllAnswersResponseDTO);
    }

    public AnswerStatusResponseDTO getUserAnswerStatus(String userId) {
        String key = "user:" + userId;
        String nickname = String.valueOf(stringRedisTemplate.opsForHash().get(key, nicknameField));
        AnswerStatus isCorrect = AnswerStatus.valueOf(String.valueOf(stringRedisTemplate.opsForHash().get(key, isCorrectField)));
        String drawingSrc = String.valueOf(stringRedisTemplate.opsForHash().get(key, drawSrcField));
        String avatarsImgSrc = String.valueOf(stringRedisTemplate.opsForHash().get(key, avatarProfileImgField));

        return AnswerStatusResponseDTO.builder()
                .userId(userId)
                .nickname(nickname)
                .isCorrect(isCorrect)
                .drawingSrc(drawingSrc)
                .avatarsImgSrc(avatarsImgSrc)
                .build();
    }

    public void confirmAnswer(String userId, boolean isConfirmed, String roomId) {
        ConfirmResponseDTO confirmResponseDTO = new ConfirmResponseDTO(GameAction.ANSWER_CONFIRMED);
        if (isConfirmed) {
            addScore(userId);
        }
        // 방장 id 가져오기
        String hostId = roomService.getHostId(roomId);
        String channelName = "user:" + hostId;
        genericMessagePublisher.publishString(channelName, confirmResponseDTO);
    }

    private void addScore(String userId) {
        String userKey = "user:" + userId;
        int currentScore = Integer.parseInt(String.valueOf(stringRedisTemplate.opsForHash().get(userKey, "score")));
        stringRedisTemplate.opsForHash().put(userKey, "score", String.valueOf(currentScore + answerScore));
    }
}
