package io.ssafy.p.k11a405.backend.service;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.ssafy.p.k11a405.backend.dto.CreateAudioTokenResponseDTO;
import io.ssafy.p.k11a405.backend.dto.RoomAction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AudioService {

    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;

    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

    private final SimpMessagingTemplate messagingTemplate;

    public CreateAudioTokenResponseDTO createToken(String userId, String roomId) {
        AccessToken accessToken = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
        accessToken.setName(userId);
        accessToken.setIdentity(userId);
        accessToken.addGrants(new RoomJoin(true), new RoomName(roomId));

        String jwtAccessToken = accessToken.toJwt();
        return new CreateAudioTokenResponseDTO(jwtAccessToken, RoomAction.CREATE_AUDIO_TOKEN);

    }
}
