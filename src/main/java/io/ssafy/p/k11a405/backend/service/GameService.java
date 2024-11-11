package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.*;
import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.entity.DialogueSituation;
import io.ssafy.p.k11a405.backend.entity.Dialogue;
import io.ssafy.p.k11a405.backend.exception.BusinessException;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessagePublisher;
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
    private final String isCorrectField = "isCorrect";
    private final String nicknameField = "nickname";
    private final String avatarProfileImgField = "avatarProfileImg";
    private final String drawSrcField = "drawingSrc";
    private final String isAgreedField = "isAgreed";

    private final GenericMessagePublisher genericMessagePublisher;
    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final DialogueService dialogueService;
    private final ScenarioService scenarioService;
    private final RoomService roomService;
    private final UserService userService;

    public void startGame(StartGameRequestDTO startGameRequestDTO) {
        String channelName = redisKeyPrefix + startGameRequestDTO.roomId() + ":start";
        // db에서 scenarioDialog 가져오기
        Integer stageNumber = startGameRequestDTO.stageNumber();
        String situationTag = ScenarioType.SITUATION.getKoreanName();
        Dialogue dialogue = dialogueService.findScenarioByStageAndSituation(stageNumber, situationTag);
        DialogueSituation dialogueSituation = dialogue.getDialogues().get(0);
        clearRoomStatus(startGameRequestDTO.roomId());

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
        List<AnswerStatusResponseDTO> answerStatuses = userIds.stream().map(this::getUserAnswerStatus).toList();
        ExplainResponseDTO explainResponseDTO = new ExplainResponseDTO(answerStatuses, GameAction.ADD_EXPLAIN_QUEUE);
        genericMessagePublisher.publishString(channelName, explainResponseDTO);
    }

    public void haveASay(HaveASayRequestDTO haveASayRequestDTO) {
        String channelName = redisKeyPrefix + haveASayRequestDTO.roomId();
        String queueKey = redisKeyPrefix + haveASayRequestDTO.roomId() + ":explanationQueue";
        String userId = stringRedisTemplate.opsForList().leftPop(queueKey);
        List<String> userIds = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        HaveASayResponseDTO haveASayResponseDTO = new HaveASayResponseDTO(userId, userIds, GameAction.HAVE_A_SAY);
        genericMessagePublisher.publishString(channelName, haveASayResponseDTO);
    }

    public void checkAllAnswers(String roomId) {
        // 각 유저의 정답을 담아야 한다. 1. 유저 아이디 리스트 조회 2. 유저 아이디에 맞는 이미지 생성
        String userKey = "rooms:" + roomId + ":users";
        Set<String> userIds = stringRedisTemplate.opsForZSet().range(userKey, 0, -1);
        List<AnswerStatusResponseDTO> answerStatuses = new ArrayList<>();
        userIds.stream().map(this::getUserAnswerStatus).forEach(answerStatuses::add);
//        List<AnswerStatusResponseDTO> answerStatuses = userIds.stream().map(this::getUserAnswerStatus).toList();

        CheckAllAnswersResponseDTO checkAllAnswersResponseDTO = new CheckAllAnswersResponseDTO(answerStatuses, GameAction.CHECK_ALL_ANSWERS);
        String channelName = redisKeyPrefix + roomId + ":allAnswers";
        genericMessagePublisher.publishString(channelName, checkAllAnswersResponseDTO);
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
        stringRedisTemplate.opsForHash().put(userKey, isCorrectField, answerStatus);

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
        String hostId = roomService.getHostId(roomId);
        // 1. 투표 정보 저장
        String userKey = "user:" + userId;
        stringRedisTemplate.opsForHash().put(userKey, isAgreedField, String.valueOf(isAgreed));
        // 2. 투표 현황 응답
        String roomKey = "rooms:" + roomId + ":users";
        Set<String> userIds = stringRedisTemplate.opsForZSet().range(roomKey, 0, -1);
        List<String> voteResults = userIds.stream()
                .map(id -> "user:" + id)
                .map(id -> String.valueOf(stringRedisTemplate.opsForHash().get(id, isAgreedField)))
                .filter(id -> !"null".equals(id))
                .toList();
        String hostKey = "user:" + hostId;
        genericMessagePublisher.publishString(hostKey, calculateVoteResult(voteResults));
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
        genericMessagePublisher.publishString(channelName, confirmResponseDTO);
    }

    public void endVote(String roomId, String userId) {
        String channelName = "games:" + roomId + ":voteEnded";
        String userKey = userService.generateUserKey(userId);
        String drawingSrc = String.valueOf(stringRedisTemplate.opsForHash().get(userKey, drawSrcField));
        Set<String> userIds = stringRedisTemplate.opsForZSet().range(userKey, 0, -1);
        List<String> voteResults = userIds.stream().map(id -> String.valueOf(stringRedisTemplate.opsForHash().get(userKey, isAgreedField))).toList();
        VoteResponseDTO voteResponseDTO = calculateVoteResult(voteResults);
        boolean isPassed = isPassed(voteResponseDTO);
        EndVoteResponseDTO endVoteResponseDTO = EndVoteResponseDTO.builder()
                .userId(userId)
                .drawingSrc(drawingSrc)
                .isPassed(isPassed)
                .action(GameAction.END_VOTE)
                .build();
        removeFromQueue(roomId, userId);

        genericMessagePublisher.publishString(channelName, endVoteResponseDTO);
    }

    public void startDrawing(String roomId) {
        String channelName = "games:" + roomId + ":startDrawing";
        StartDrawingResponseDTO startDrawingResponseDTO = new StartDrawingResponseDTO(GameAction.DRAWING_START);

        genericMessagePublisher.publishString(channelName, startDrawingResponseDTO);
    }

    private AnswerStatusResponseDTO getUserAnswerStatus(String userId) {
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

    private VoteResponseDTO calculateVoteResult(List<String> voteResults) {
        int proCount = 0;
        int conCount = 0;
        for (String voteResult : voteResults) {
            boolean result = Boolean.parseBoolean(voteResult);
            if (result) {
                ++proCount;
            } else {
                ++conCount;
            }
        }
        return new VoteResponseDTO(proCount, conCount, GameAction.VOTE);
    }

    private boolean isPassed(VoteResponseDTO voteResponseDTO) {
        return voteResponseDTO.proCount() >= voteResponseDTO.conCount();
    }

    private void removeFromQueue(String roomId, String targetUserId) {
        String queueKey = redisKeyPrefix + roomId + ":explanationQueue";
        List<String> explanationQueue = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        stringRedisTemplate.delete(queueKey);
        explanationQueue.stream().filter(userId -> !userId.equals(targetUserId)).forEach(userId -> {
            stringRedisTemplate.opsForList().rightPush(queueKey, userId);
        });
    }
}
