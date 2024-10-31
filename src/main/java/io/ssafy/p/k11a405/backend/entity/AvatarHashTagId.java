package io.ssafy.p.k11a405.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class AvatarHashTagId {

    @ManyToOne
    @JoinColumn(name = "avatars_id", referencedColumnName = "id")
    private Avatars avatarsId;

    @ManyToOne
    @JoinColumn(name = "hashtags_id", referencedColumnName = "id")
    private HashTags hashTagsId;
}
