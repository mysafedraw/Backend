package io.ssafy.p.k11a405.backend.repository;

import io.ssafy.p.k11a405.backend.entity.AvatarHashTag;
import io.ssafy.p.k11a405.backend.entity.AvatarHashTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvatarHashTagRepository extends JpaRepository<AvatarHashTag, AvatarHashTagId> {

    @Query(value = "SELECT * FROM avatar_hash_tag WHERE avatars_id = :avatarsId", nativeQuery = true)
    List<AvatarHashTag> findByAvatarsId(Integer avatarsId);
}
