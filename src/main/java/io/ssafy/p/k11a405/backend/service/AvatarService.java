package io.ssafy.p.k11a405.backend.service;

import io.ssafy.p.k11a405.backend.dto.FindAvatarsInfoResponseDTO;
import io.ssafy.p.k11a405.backend.dto.FindAvatarsResponseDTO;
import io.ssafy.p.k11a405.backend.entity.AvatarHashTag;
import io.ssafy.p.k11a405.backend.entity.AvatarHashTagId;
import io.ssafy.p.k11a405.backend.entity.Avatars;
import io.ssafy.p.k11a405.backend.entity.HashTags;
import io.ssafy.p.k11a405.backend.exception.BusinessException;
import io.ssafy.p.k11a405.backend.exception.ErrorCode;
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

        for (Avatars avatar : avatarsList) {
            List<AvatarHashTag> avatarHashTagList = avatarHashTagRepository.findByAvatarsId(avatar.getId());
            List<String> hashTagNameList = avatarHashTagList.stream()
                    .map(AvatarHashTag::getId)
                    .map(AvatarHashTagId::getHashTagsId)
                    .map(HashTags::getName)
                    .toList();

            FindAvatarsResponseDTO findAvatarsResponseDTO = FindAvatarsResponseDTO.builder()
                    .id(avatar.getId())
                    .avatarName(avatar.getName())
                    .hashTagNameList(hashTagNameList)
                    .profileImg(avatar.getProfileImg())
                    .build();

            findAvatarsResponseDTOList.add(findAvatarsResponseDTO);
        }

        return findAvatarsResponseDTOList;
    }

    @Transactional
    public FindAvatarsInfoResponseDTO findAvatarInfo(Integer avatarsId) {
        Avatars avatars = avatarRepository.findById(avatarsId).orElseThrow(() -> new BusinessException(ErrorCode.AVATAR_NOT_FOUND));

        return FindAvatarsInfoResponseDTO.builder()
                .assetImg(avatars.getProfileImg())
                .feature(avatars.getFeature())
                .name(avatars.getName())
                .profileImg(avatars.getProfileImg())
                .build();
    }
}
