package io.ssafy.p.k11a405.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final String drawSrcField = "drawSrc";

    private final StringRedisTemplate stringRedisTemplate;

    public void uploadAnswerDrawing(MultipartFile file, String userId) throws IOException {
        String userKey = "rooms:" + userId;
        // 실제로는 이미지를 저장하고, 그 경로를 가져와야 한다.
        String drawingSrc = "https://cdn.sortiraparis.com/images/1001/69688/1076636-venom-the-last-dance-le-dernier-chapitre-de-la-trilogie-marvel-avec-tom-hardy-se-devoile.jpg";

        stringRedisTemplate.opsForHash().put(userKey, drawSrcField, drawingSrc);
    }
}
