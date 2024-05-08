package com.ddudu.application.domain.user.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;

import com.ddudu.application.domain.user.exception.UserErrorCode;
import com.google.common.collect.Lists;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  private static final int MAX_NICKNAME_LENGTH = 20;
  public static final int MAX_USERNAME_LENGTH = 30;
  private static final int MAX_INTRODUCTION_LENGTH = 50;

  @EqualsAndHashCode.Include
  private final Long id;
  private final String username;
  private final String nickname;
  private final String introduction;
  private final Authority authority;
  private final UserStatus status;
  @Getter(AccessLevel.NONE)
  private final Options options;
  private final List<AuthProvider> authProviders;

  @Builder
  public User(
      Long id, String username, String nickname, String introduction, Authority authority,
      UserStatus status, List<AuthProvider> authProviders, Options options,
      Boolean allowingFollowsAfterApproval, Boolean templateNotification, Boolean dduduNotification
  ) {
    validate(nickname, username, introduction);

    this.id = id;
    this.username = username;
    this.nickname = nickname;
    this.authority = Objects.requireNonNullElse(authority, Authority.NORMAL);
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    this.status = isNull(status) ? UserStatus.ACTIVE : status;
    this.options = Objects.nonNull(options) ? options
        : buildOptions(allowingFollowsAfterApproval, templateNotification, dduduNotification);
    this.authProviders = isNull(authProviders) ? Lists.newArrayList() : authProviders;
  }

  public boolean isAllowingFollowsAfterApproval() {
    return this.options.isAllowingFollowsAfterApproval();
  }

  public boolean isNotifyingTemplate() {
    return this.options.isTemplateNotification();
  }

  public boolean isNotifyingDdudu() {
    return this.options.isDduduNotification();
  }

  public User applyProfileUpdate(String nickname, String introduction) {
    return User.builder()
        .id(this.id)
        .username(this.username)
        .nickname(nickname)
        .authority(this.authority)
        .introduction(introduction)
        .status(this.status)
        .options(this.options)
        .build();
  }

  public void switchOptions() {
    options.switchOptions();
  }

  private void validate(String nickname, String username, String introduction) {
    validateNickname(nickname);
    validateUsername(username);

    if (Objects.nonNull(introduction)) {
      validateIntroduction(introduction);
    }
  }

  private void validateNickname(String nickname) {
    checkArgument(StringUtils.isNotBlank(nickname), UserErrorCode.BLANK_NICKNAME.name());
    checkArgument(
        nickname.length() <= MAX_NICKNAME_LENGTH, UserErrorCode.EXCESSIVE_NICKNAME_LENGTH.name());
  }

  private void validateUsername(String username) {
    checkArgument(StringUtils.isNotBlank(username), UserErrorCode.BLANK_USERNAME.name());
    checkArgument(
        username.length() <= MAX_USERNAME_LENGTH, UserErrorCode.EXCESSIVE_NICKNAME_LENGTH.name());
  }

  private void validateIntroduction(String introduction) {
    checkArgument(
        introduction.length() <= MAX_INTRODUCTION_LENGTH,
        UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH.name()
    );
  }

  private Options buildOptions(
      Boolean allowingFollowsAfterApproval, Boolean templateNotification, Boolean dduduNotification
  ) {
    return Options.builder()
        .allowingFollowsAfterApproval(allowingFollowsAfterApproval)
        .dduduNotification(dduduNotification)
        .templateNotification(templateNotification)
        .build();
  }

}
