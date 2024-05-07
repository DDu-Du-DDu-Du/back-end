package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.user.domain.Authority;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.UserStatus;
import com.ddudu.old.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "nickname", length = 20, nullable = false)
  private String nickname;

  @Column(name = "username", length = 20, unique = true)
  private String username;

  @Column(name = "introduction", length = 50)
  private String introduction;

  @Column(name = "profile_image_url", length = 1024)
  private String profile_image_url;

  @Column(name = "authority", columnDefinition = "VARCHAR", length = 15)
  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(name = "status", columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(name = "follows_after_approval", nullable = false)
  private boolean allowingFollowsAfterApproval;

  @Column(name = "template_notification", nullable = false)
  private boolean templateNotification;

  @Column(name = "ddudu_notification", nullable = false)
  private boolean dduduNotification;

  public static UserEntity from(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .username(user.getUsername())
        .introduction(user.getIntroduction())
        .authority(user.getAuthority())
        .status(user.getStatus())
        .allowingFollowsAfterApproval(user.isAllowingFollowsAfterApproval())
        .templateNotification(user.isNotifyingTemplate())
        .dduduNotification(user.isNotifyingDdudu())
        .build();
  }

  public User toDomain() {
    return User.builder()
        .id(id)
        .username(username)
        .nickname(nickname)
        .introduction(introduction)
        .authority(authority)
        .status(status)
        .allowingFollowsAfterApproval(allowingFollowsAfterApproval)
        .templateNotification(templateNotification)
        .dduduNotification(dduduNotification)
        .build();
  }

}
