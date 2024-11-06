package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.*;
import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.entity.DialogueSituation;
import io.ssafy.p.k11a405.backend.entity.Dialogue;
import io.ssafy.p.k11a405.backend.pubsub.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final String redisKeyPrefix = "games:";

    private final MessagePublisher messagePublisher;
    private final StringRedisTemplate stringRedisTemplate;

    private final DialogueService dialogueService;

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

    public void haveASay(HaveASayRequestDTO haveASayRequestDTO) {
        String channelName = redisKeyPrefix + haveASayRequestDTO.roomId();
        String queueKey = redisKeyPrefix + haveASayRequestDTO.roomId() + ":explanationQueue";
        String userId = stringRedisTemplate.opsForList().leftPop(queueKey);
        List<String> userIds = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        HaveASayResponseDTO haveASayResponseDTO = new HaveASayResponseDTO(userId, userIds, GameAction.HAVE_A_SAY);
        messagePublisher.publish(channelName, haveASayResponseDTO);
    }

    public void checkAllAnswers(CheckAllAnswersRequestDTO checkAllAnswersRequestDTO) {
        List<AnswerStatusResponseDTO> answerStatuses = new ArrayList<>();
        answerStatuses.add(AnswerStatusResponseDTO.builder()
                        .avatarsImgSrc("https://cdn.inflearn.com/public/main/profile/default_profile.png")
                        .drawSrc("https://cdn.inflearn.com/public/main_sliders/1c333cda-3dcf-46c4-be01-46b6f99ae750/I_O_python_1.png")
                        .isCorrect(true)
                        .nickname("하이")
                        .userId("userID")
                .build());
        CheckAllAnswersResponseDTO checkAllAnswersResponseDTO = new CheckAllAnswersResponseDTO(answerStatuses, GameAction.CHECK_ALL_ANSWERS);
        String channelName = redisKeyPrefix + checkAllAnswersRequestDTO.roomId();
        messagePublisher.publish(channelName, checkAllAnswersResponseDTO);
    }

    public void checkAnswer(String roomId, String userId, String scenarioId, String userAnswer) {
        // 정답 체크

        // 유저 정답 여부 redis 저장
        String userKey = "user:" + userId;
        stringRedisTemplate.opsForHash().put(userKey, "isCorrect", true);

        // 해당 유저에게 정답 여부 전송
        String channelName = "rooms:" + userId + ":" + "users";
        messagePublisher.publish(channelName, new CheckAnswerResponseDTO(userId, true));
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
}
