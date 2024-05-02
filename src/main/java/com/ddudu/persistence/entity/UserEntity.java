package com.ddudu.persistence.entity;

import com.ddudu.application.auth.domain.authority.Authority;
import com.ddudu.application.common.BaseEntity;
import com.ddudu.application.user.domain.Options;
import com.ddudu.application.user.domain.User;
import com.ddudu.application.user.domain.UserStatus;
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
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@SQLRestriction("is_deleted = 0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "optional_username", length = 20, unique = true)
  private String optionalUsername;

  @Column(name = "email", length = 50, nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String encrypted;

  @Column(name = "nickname", length = 20, nullable = false)
  private String nickname;

  @Column(name = "introduction", length = 50)
  private String introduction;

  @Column(name = "authority", columnDefinition = "VARCHAR", length = 15)
  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(name = "status", columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(name = "follows_after_approval", nullable = false)
  private boolean allowingFollowsAfterApproval;

  @Builder
  public UserEntity(
      Long id, String optionalUsername, String email, String encrypted, String nickname,
      String introduction, Authority authority, UserStatus status,
      boolean allowingFollowsAfterApproval, LocalDateTime createdAt, LocalDateTime updatedAt,
      Boolean isDeleted
  ) {
    super(createdAt, updatedAt, isDeleted);

    this.id = id;
    this.optionalUsername = optionalUsername;
    this.email = email;
    this.encrypted = encrypted;
    this.nickname = nickname;
    this.introduction = introduction;
    this.authority = authority;
    this.status = status;
    this.allowingFollowsAfterApproval = allowingFollowsAfterApproval;
  }

  public static UserEntity from(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .optionalUsername(user.getOptionalUsername())
        .email(user.getEmail())
        .encrypted(user.getPassword()
            .getEncrypted())
        .nickname(user.getNickname())
        .introduction(user.getIntroduction())
        .authority(user.getAuthority())
        .status(user.getStatus())
        .allowingFollowsAfterApproval(user.getOptions()
            .isAllowingFollowsAfterApproval())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .isDeleted(user.isDeleted())
        .build();
  }

  public User toDomain() {
    return User.builder()
        .id(id)
        .optionalUsername(optionalUsername)
        .email(email)
        .encryptedPassword(encrypted)
        .nickname(nickname)
        .introduction(introduction)
        .authority(authority)
        .status(status)
        .options(new Options(allowingFollowsAfterApproval))
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .isDeleted(isDeleted())
        .build();
  }

}
