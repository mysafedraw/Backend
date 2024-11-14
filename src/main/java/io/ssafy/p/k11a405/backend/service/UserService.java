package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.FindAvatarsInfoResponseDTO;
import io.ssafy.p.k11a405.backend.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final String idField = "id";
    private final String avatarIdField = "avatarId";
    private final String nicknameField = "nickname";
    private final String avatarProfileImgField = "avatarProfileImg";
    private final String scoreField = "score";

    private final StringRedisTemplate stringRedisTemplate;
    private final AvatarService avatarService;

    public UserResponseDTO createUser(String nickname, Integer avatarId) {
        // 고유한 UUID 생성
        String userId = UUID.randomUUID().toString();
        // Redis 해시에 유저 정보 저장
        String userKey = "user:" + userId;
        FindAvatarsInfoResponseDTO avatarInfo = avatarService.findAvatarInfo(avatarId);
        // Redis에 유저 데이터 저장
        stringRedisTemplate.opsForHash().put(userKey, idField, userId);
        stringRedisTemplate.opsForHash().put(userKey, nicknameField, nickname);
        stringRedisTemplate.opsForHash().put(userKey, avatarIdField, String.valueOf(avatarId)); // 아바타 id 저장
        stringRedisTemplate.opsForHash().put(userKey, avatarProfileImgField, avatarInfo.profileImg());

        return new UserResponseDTO(userId, nickname, avatarInfo.profileImg(), 0); // 생성된 유저 ID 반환
    }

    public UserResponseDTO updateNickname(String nickname, String userId) {
        String userKey = generateUserKey(userId);
        stringRedisTemplate.opsForHash().put(userKey, nicknameField, nickname);
        return getUserInfoByUserId(userId);
    }

    public UserResponseDTO getUserInfoByUserId(String userId) {
        String userKey = generateUserKey(userId);
        String nickname = String.valueOf(stringRedisTemplate.opsForHash().get(userKey, nicknameField));
        String profileImg = String.valueOf(stringRedisTemplate.opsForHash().get(userKey, avatarProfileImgField));
        Integer score = Integer.parseInt(String.valueOf(stringRedisTemplate.opsForHash().get(userKey, scoreField)));
        return new UserResponseDTO(userId, nickname, profileImg, score);
    }

    public String generateUserKey(String userId) {
        return "user:" + userId;
    }

    public Set<String> getUserIdsInRoom(String roomId) {
        String roomKey = "rooms:" + roomId + ":users";
        return stringRedisTemplate.opsForZSet().range(roomKey, 0, -1);
    }
}