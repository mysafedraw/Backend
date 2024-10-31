package io.ssafy.p.k11a405.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class AvatarHashTag {

    @EmbeddedId
    private AvatarHashTagId id;
}
