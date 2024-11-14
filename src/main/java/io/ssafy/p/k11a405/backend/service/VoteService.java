package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.game.*;
import io.ssafy.p.k11a405.backend.pubsub.GenericMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final String redisKeyPrefix = "games:";
    private final String isAgreedField = "isAgreed";
    private final String drawSrcField = "drawingSrc";

    private final StringRedisTemplate stringRedisTemplate;
    private final GenericMessagePublisher genericMessagePublisher;

    private final UserService userService;
    private final RoomService roomService;
    private final AnswerService answerService;

    public void vote(String roomId, boolean isAgreed, String userId) {
        // 방장 아이디 가져오기
        String hostId = roomService.getHostId(roomId);
        // 1. 투표 정보 저장
        String userKey = "user:" + userId;
        stringRedisTemplate.opsForHash().put(userKey, isAgreedField, String.valueOf(isAgreed));
        // 2. 투표 현황 응답
        Set<String> userIds = userService.getUserIdsInRoom(roomId);
        List<String> voteResults = extractVoteResults(userIds);
        String hostKey = "user:" + hostId;
        genericMessagePublisher.publishString(hostKey, calculateVoteResult(voteResults));
    }

    public void endVote(String roomId, String userId) {
        String channelName = "games:" + roomId + ":voteEnded";
        String userKey = userService.generateUserKey(userId);
        String drawingSrc = String.valueOf(stringRedisTemplate.opsForHash().get(userKey, drawSrcField));
        Set<String> userIds = userService.getUserIdsInRoom(roomId);
        List<String> voteResults = extractVoteResults(userIds);
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
        refreshWaitingQueue(roomId);
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

    private List<String> extractVoteResults(Set<String> userIds) {
        return userIds.stream()
                .map(id -> "user:" + id)
                .map(id -> String.valueOf(stringRedisTemplate.opsForHash().get(id, isAgreedField)))
                .filter(id -> !"null".equals(id))
                .toList();
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

    private void refreshWaitingQueue(String roomId) {
        String queueKey = redisKeyPrefix + roomId + ":explanationQueue";
        List<String> userIds = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        List<AnswerStatusResponseDTO> answerStatuses = userIds.stream().map(answerService::getUserAnswerStatus).toList();
        ExplainResponseDTO explainResponseDTO = new ExplainResponseDTO(answerStatuses, GameAction.ADD_EXPLAIN_QUEUE);
        String channelName = redisKeyPrefix + roomId + ":explainQueue";
        genericMessagePublisher.publishString(channelName, explainResponseDTO);
    }
}
