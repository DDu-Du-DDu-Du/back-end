package com.ddudu.infra.mysql.user.user.entity;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.User.UserBuilder;
import com.ddudu.domain.user.user.aggregate.enums.Authority;
import com.ddudu.domain.user.user.aggregate.enums.UserStatus;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "nickname",
      length = 20,
      nullable = false
  )
  private String nickname;

  @Column(
      name = "username",
      length = 30,
      unique = true
  )
  private String username;

  @Column(
      name = "introduction",
      length = 50
  )
  private String introduction;

  @Column(
      name = "profile_image_url",
      length = 1024
  )
  private String profileImageUrl;

  @Column(
      name = "authority",
      columnDefinition = "VARCHAR",
      length = 15
  )
  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(
      name = "status",
      columnDefinition = "VARCHAR",
      length = 20
  )
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(
      name = "follows_after_approval",
      nullable = false
  )
  private boolean allowingFollowsAfterApproval;

  @Column(
      name = "template_notification",
      nullable = false
  )
  private boolean templateNotification;

  @Column(
      name = "ddudu_notification",
      nullable = false
  )
  private boolean dduduNotification;

  public static UserEntity from(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .username(user.getUsername())
        .introduction(user.getIntroduction())
        .authority(user.getAuthority())
        .status(user.getStatus())
        .profileImageUrl(user.getProfileImageUrl())
        .allowingFollowsAfterApproval(user.isAllowingFollowsAfterApproval())
        .templateNotification(user.isNotifyingTemplate())
        .dduduNotification(user.isNotifyingDdudu())
        .build();
  }

  public User toDomain() {
    return buildUser().build();
  }

  public User toDomainWith(List<AuthProvider> authProviders) {
    return buildUser()
        .authProviders(authProviders)
        .build();
  }

  private UserBuilder buildUser() {
    return User.builder()
        .id(id)
        .username(username)
        .nickname(nickname)
        .introduction(introduction)
        .authority(authority)
        .status(status)
        .profileImageUrl(profileImageUrl)
        .allowingFollowsAfterApproval(allowingFollowsAfterApproval)
        .templateNotification(templateNotification)
        .dduduNotification(dduduNotification);
  }

}
