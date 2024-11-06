package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.*;
import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.entity.DialogueSituation;
import io.ssafy.p.k11a405.backend.entity.Dialogue;
import io.ssafy.p.k11a405.backend.exception.BusinessException;
import io.ssafy.p.k11a405.backend.pubsub.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final String redisKeyPrefix = "games:";
    private final String idField = "id";
    private final String avatarIdField = "avatarId";
    private final String nicknameField = "nickname";
    private final String avatarProfileImgField = "avatarProfileImg";
    private final String drawSrcField = "drawSrc";

    private final MessagePublisher messagePublisher;
    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final DialogueService dialogueService;
    private final ScenarioService scenarioService;

    public void startGame(StartGameRequestDTO startGameRequestDTO) {
        String channelName = redisKeyPrefix + "start:" + startGameRequestDTO.roomId();
        // db에서 scenarioDialog 가져오기
        Integer stageNumber = startGameRequestDTO.stageNumber();
        String situationTag = ScenarioType.SITUATION.getKoreanName();
        Dialogue dialogue = dialogueService.findScenarioByStageAndSituation(stageNumber, situationTag);
        DialogueSituation dialogueSituation = dialogue.getDialogues().get(0);

        StartGameResponseDTO startGameResponseDTO = new StartGameResponseDTO(dialogueSituation.getSituationDialogue(), GameAction.GAME_START);
        messagePublisher.publish(channelName, startGameResponseDTO);
    }

    public void addToExplanationQueue(ExplainRequestDTO explainRequestDTO) {
        String channelName = redisKeyPrefix + "explainQueue:" + explainRequestDTO.roomId();
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
        List<AnswerStatusResponseDTO> answerStatuses = userIds.stream().map(this::getUserAnswerStatus).toList();
        ExplainResponseDTO explainResponseDTO = new ExplainResponseDTO(answerStatuses, GameAction.ADD_EXPLAIN_QUEUE);
        messagePublisher.publish(channelName, explainResponseDTO);
    }

    public void haveASay(HaveASayRequestDTO haveASayRequestDTO) {
        String channelName = redisKeyPrefix + haveASayRequestDTO.roomId();
        String queueKey = redisKeyPrefix + haveASayRequestDTO.roomId() + ":explanationQueue";
        String userId = stringRedisTemplate.opsForList().leftPop(queueKey);
        List<String> userIds = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        HaveASayResponseDTO haveASayResponseDTO = new HaveASayResponseDTO(userId, userIds, GameAction.HAVE_A_SAY);
        messagePublisher.publish(channelName, haveASayResponseDTO);
    }

    public void checkAllAnswers(String roomId) {

        // 각 유저의 정답을 담아야 한다. 1. 유저 아이디 리스트 조회 2. 유저 아이디에 맞는 이미지 생성
        String userKey = "rooms:" + roomId + ":users";
        Set<String> userIds = stringRedisTemplate.opsForZSet().range(userKey, 0, -1);
        List<AnswerStatusResponseDTO> answerStatuses = userIds.stream().map(this::getUserAnswerStatus).toList();

        CheckAllAnswersResponseDTO checkAllAnswersResponseDTO = new CheckAllAnswersResponseDTO(answerStatuses, GameAction.CHECK_ALL_ANSWERS);
        String channelName = redisKeyPrefix + roomId;
        messagePublisher.publish(channelName, checkAllAnswersResponseDTO);
    }

    public void checkAnswer(String roomId, String userId, Integer scenarioId, String userAnswer, Integer stageNumber) {
        // 정답 체크
        AnswerStatus answerStatus;
        try {
            Boolean isCorrect = scenarioService.findAssetValidations(stageNumber, userAnswer, scenarioId);
            answerStatus = AnswerStatus.getAnswerStatus(isCorrect);
        } catch (BusinessException e) {
            answerStatus = AnswerStatus.INCORRECT_ANSWER;
        }

        // 유저 정답 여부 redis 저장
        String userKey = "user:" + userId;
        stringRedisTemplate.opsForHash().put(userKey, "isCorrect", answerStatus);

        InGameResponseDTO inGameResponseDTO = InGameResponseDTO.builder()
                .userId(userId)
                .isCorrect(answerStatus)
                .action(GameAction.CHECK_ANSWER)
                .build();
        // 해당 유저에게 다이렉트로 정답 여부 전송
        simpMessagingTemplate.convertAndSend("/games/" + userId, inGameResponseDTO);
    }

    public void vote(String roomId, boolean isAgreed, String userId) {
        // 방장 아이디 가져오기
        String hostId = "123123";
        String channelName = "rooms:" + hostId + ":users";
        messagePublisher.publish(channelName, new VoteResponseDTO(userId, isAgreed));
    }

    public void confirmAnswer(String userId, boolean isConfirmed, String roomId) {
        ConfirmResponseDTO confirmResponseDTO = new ConfirmResponseDTO(GameAction.ANSWER_CONFIRMED);
        if (isConfirmed) {
            // 유저 점수 가져오기

            // 유저 점수 업데이트

        }
        // 방장 id 가져오기
        String hostId = "hostId";
        String channelName = "rooms:" + hostId + ":users";
        messagePublisher.publish(channelName, confirmResponseDTO);
    }

    private AnswerStatusResponseDTO getUserAnswerStatus(String userId) {
        String key = "user:" + userId;
        String nickname = String.valueOf(stringRedisTemplate.opsForHash().get(key, nicknameField));
        AnswerStatus isCorrect = AnswerStatus.valueOf(String.valueOf(stringRedisTemplate.opsForHash().get(key, idField)));
        String drawSrc = String.valueOf(stringRedisTemplate.opsForHash().get(key, drawSrcField));
        String avatarsImgSrc = String.valueOf(stringRedisTemplate.opsForHash().get(key, avatarProfileImgField));

        return AnswerStatusResponseDTO.builder()
                .userId(userId)
                .nickname(nickname)
                .isCorrect(isCorrect)
                .drawSrc(drawSrc)
                .avatarsImgSrc(avatarsImgSrc)
                .build();
    }
}
