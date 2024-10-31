package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.FindAvatarsResponseDTO;
import io.ssafy.p.k11a405.backend.entity.AvatarHashTag;
import io.ssafy.p.k11a405.backend.entity.Avatars;
import io.ssafy.p.k11a405.backend.entity.HashTags;
import io.ssafy.p.k11a405.backend.repository.AvatarHashTagRepository;
import io.ssafy.p.k11a405.backend.repository.AvatarRepository;
import io.ssafy.p.k11a405.backend.repository.HashTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final AvatarHashTagRepository avatarHashTagRepository;
    private final HashTagRepository hashTagRepository;

    @Transactional
    public List<FindAvatarsResponseDTO> findAvatarList() {
        List<FindAvatarsResponseDTO> findAvatarsResponseDTOList = new ArrayList<>();

        //일단 캐릭터 전부 가져오고, 각각에 맞는 해시태그들 가져오기
        List<Avatars> avatarsList = avatarRepository.findAll();
        List<String> hashTagNameList = new ArrayList<>();

        for (Avatars avatar : avatarsList) {
            //해당 아바타의 해시태그만 가져오기
            List<Integer> hashTagIdList = avatarHashTagRepository.findByAvatarsId(avatar.getId())
                    .stream().map(avatarHashTag-> avatarHashTag.getId().getAvatarsId().getId()).toList();

            //id로 name 가져오기
            for(int h: hashTagIdList) {
                Optional<HashTags> name = hashTagRepository.findById(h);
                name.ifPresent(hashTags -> hashTagNameList.add(hashTags.getName()));
            }

            FindAvatarsResponseDTO findAvatarsResponseDTO = FindAvatarsResponseDTO.builder()
                    .avatarName(avatar.getName())
                    .hashTagNameList(hashTagNameList)
                    .profileImg(avatar.getProfileImg())
                    .build();

            findAvatarsResponseDTOList.add(findAvatarsResponseDTO);
        }

        return findAvatarsResponseDTOList;
    }
}
