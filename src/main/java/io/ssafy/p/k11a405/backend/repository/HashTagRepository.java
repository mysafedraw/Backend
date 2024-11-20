package io.ssafy.p.k11a405.backend.repository;

import io.ssafy.p.k11a405.backend.entity.HashTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashTagRepository extends JpaRepository<HashTags, Integer> {
}
