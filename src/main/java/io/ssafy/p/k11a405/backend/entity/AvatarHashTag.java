package io.ssafy.p.k11a405.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class AvatarHashTag {

    @ManyToOne
    @JoinColumn(name="id")
    private Avatars avatarsId;

    @ManyToOne
    @JoinColumn(name="id")
    private HashTags hashTagsId;
}
