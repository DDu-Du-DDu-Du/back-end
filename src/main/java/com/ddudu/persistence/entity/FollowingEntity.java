package com.ddudu.persistence.entity;

import com.ddudu.application.common.BaseEntity;
import com.ddudu.application.user.domain.Following;
import com.ddudu.application.user.domain.FollowingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "followings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowingEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", referencedColumnName = "id", nullable = false)
  private UserEntity follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "followee_id", referencedColumnName = "id", nullable = false)
  private UserEntity followee;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private FollowingStatus status;

  @Builder
  public FollowingEntity(
    Long id, UserEntity follower, UserEntity followee, FollowingStatus status,
    LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);

    this.id = id;
    this.follower = follower;
    this.followee = followee;
    this.status = status;
  }

  public static FollowingEntity from(Following following) {
    return FollowingEntity.builder()
      .id(following.getId())
      .follower(UserEntity.from(following.getFollower()))
      .followee(UserEntity.from(following.getFollowee()))
      .status(following.getStatus())
      .createdAt(following.getCreatedAt())
      .updatedAt(following.getUpdatedAt())
      .build();
  }

  public Following toDomain() {
    return Following.builder()
      .id(id)
      .follower(follower.toDomain())
      .followee(followee.toDomain())
      .status(status)
      .createdAt(getCreatedAt())
      .updatedAt(getUpdatedAt())
      .build();
  }

}
