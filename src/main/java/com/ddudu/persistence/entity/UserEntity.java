package com.ddudu.persistence.entity;

import static java.util.Objects.isNull;

import com.ddudu.application.auth.domain.authority.Authority;
import com.ddudu.application.common.BaseEntity;
import com.ddudu.application.user.domain.Options;
import com.ddudu.application.user.domain.User;
import com.ddudu.application.user.domain.UserStatus;
import com.ddudu.persistence.util.FakeValueGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

  private static final Authority DEFAULT_AUTHORITY = Authority.NORMAL;
  private static final UserStatus DEFAULT_STATUS = UserStatus.ACTIVE;
  private static final boolean DEFAULT_ALLOWING_FOLLOWS_AFTER_APPROVAL = false;
  private static final boolean DEFAULT_TEMPLATE_NOTIFICATION = true;
  private static final boolean DEFAULT_DDUDU_NOTIFICATION = true;

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

  @Builder
  public UserEntity(
      Long id, String nickname, String username, String introduction, String profile_image_url,
      Authority authority, UserStatus status, Boolean allowingFollowsAfterApproval,
      Boolean templateNotification, Boolean dduduNotification,
      LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);

    this.id = id;
    this.nickname = nickname;
    this.username = username;
    this.introduction = introduction;
    this.profile_image_url = profile_image_url;
    this.authority = isNull(authority) ? DEFAULT_AUTHORITY : authority;
    this.status = isNull(status) ? DEFAULT_STATUS : status;
    this.allowingFollowsAfterApproval =
        isNull(allowingFollowsAfterApproval) ? DEFAULT_ALLOWING_FOLLOWS_AFTER_APPROVAL
            : allowingFollowsAfterApproval;
    this.templateNotification = isNull(templateNotification) ? DEFAULT_TEMPLATE_NOTIFICATION
        : templateNotification;
    this.dduduNotification =
        isNull(dduduNotification) ? DEFAULT_DDUDU_NOTIFICATION : dduduNotification;
  }

  public static UserEntity from(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .username(isNull(user.getOptionalUsername()) ? FakeValueGenerator.username()
            : user.getOptionalUsername())
        .introduction(user.getIntroduction())
        .authority(user.getAuthority())
        .status(user.getStatus())
        .allowingFollowsAfterApproval(user.getOptions()
            .isAllowingFollowsAfterApproval())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }

  public User toDomain() {
    return User.builder()
        .id(id)
        .optionalUsername(username)
        .email(FakeValueGenerator.email())
        .encryptedPassword(FakeValueGenerator.password())
        .nickname(nickname)
        .introduction(introduction)
        .authority(authority)
        .status(status)
        .options(new Options(allowingFollowsAfterApproval))
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .build();
  }

}
