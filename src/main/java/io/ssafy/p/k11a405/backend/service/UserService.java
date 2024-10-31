package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final StringRedisTemplate stringRedisTemplate;

    public UserResponseDTO createUser(String nickname) {
        // 고유한 UUID 생성
        String userUUID = UUID.randomUUID().toString();
        // Redis 해시에 유저 정보 저장
        String userKey = "user:" + userUUID;
        // Redis에 유저 데이터 저장
        stringRedisTemplate.opsForHash().put(userKey, "id", userUUID);
        stringRedisTemplate.opsForHash().put(userKey, "nickname", nickname);

        return new UserResponseDTO(userUUID, nickname); // 생성된 유저 ID 반환
    }
}