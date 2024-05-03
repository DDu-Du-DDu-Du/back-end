package com.ddudu.persistence.entity;

import com.ddudu.application.common.BaseEntity;
import com.ddudu.application.user.domain.Following;
import com.ddudu.application.user.domain.FollowingStatus;
import com.ddudu.persistence.util.FakeValueGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "followings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowingEntity extends BaseEntity {

  @EmbeddedId
  private FollowingId id;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private FollowingStatus status;

  @Builder
  public FollowingEntity(
      UserEntity follower, UserEntity followee, FollowingStatus status,
      LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);

    this.id = new FollowingId(follower, followee);
    this.status = status;
  }

  public static FollowingEntity from(Following following) {
    return FollowingEntity.builder()
        .follower(UserEntity.from(following.getFollower()))
        .followee(UserEntity.from(following.getFollowee()))
        .status(following.getStatus())
        .createdAt(following.getCreatedAt())
        .updatedAt(following.getUpdatedAt())
        .build();
  }

  public Following toDomain() {
    return Following.builder()
        .id(FakeValueGenerator.id())
        .follower(id.getFollower()
            .toDomain())
        .followee(id.getFollowee()
            .toDomain())
        .status(status)
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .build();
  }

}
