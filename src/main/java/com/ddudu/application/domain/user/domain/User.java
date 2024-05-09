package com.ddudu.application.domain.user.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.user.domain.enums.Authority;
import com.ddudu.application.domain.user.domain.enums.UserStatus;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import com.ddudu.application.domain.user.domain.vo.Options;
import com.ddudu.application.domain.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import java.util.Collections;
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
  private static final int MAX_URL_LENGTH = 1024;

  @EqualsAndHashCode.Include
  private final Long id;
  private final String username;
  private final String nickname;
  private final String introduction;
  private final Authority authority;
  private final UserStatus status;
  private final String profileImageUrl;

  @Getter(AccessLevel.NONE)
  private final Options options;
  private final List<AuthProvider> authProviders;

  @Builder
  private User(
      Long id, String username, String nickname, String introduction, Authority authority,
      UserStatus status, String profileImageUrl, List<AuthProvider> authProviders, Options options,
      Boolean allowingFollowsAfterApproval, Boolean templateNotification, Boolean dduduNotification
  ) {
    validate(nickname, username, introduction, profileImageUrl);

    this.id = id;
    this.username = username;
    this.nickname = nickname;
    this.authority = Objects.requireNonNullElse(authority, Authority.NORMAL);
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    this.status = Objects.requireNonNullElse(status, UserStatus.ACTIVE);
    this.profileImageUrl = profileImageUrl;
    this.options = Objects.requireNonNullElse(
        options, buildOptions(allowingFollowsAfterApproval, templateNotification, dduduNotification)
    );
    this.authProviders = Objects.requireNonNullElse(authProviders, Collections.emptyList());
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
        .profileImageUrl(this.profileImageUrl)
        .options(this.options)
        .build();
  }

  public void switchOptions() {
    options.switchOptions();
  }

  private void validate(
      String nickname, String username, String introduction, String profileImageUrl
  ) {
    validateNickname(nickname);
    validateUsername(username);

    if (Objects.nonNull(introduction)) {
      validateIntroduction(introduction);
    }

    if (Objects.nonNull(profileImageUrl)) {
      validateUrl(profileImageUrl);
    }
  }

  private void validateNickname(String nickname) {
    checkArgument(StringUtils.isNotBlank(nickname), UserErrorCode.BLANK_NICKNAME.getCodeName());
    checkArgument(
        nickname.length() <= MAX_NICKNAME_LENGTH,
        UserErrorCode.EXCESSIVE_NICKNAME_LENGTH.getCodeName()
    );
  }

  private void validateUsername(String username) {
    checkArgument(StringUtils.isNotBlank(username), UserErrorCode.BLANK_USERNAME.getCodeName());
    checkArgument(
        username.length() <= MAX_USERNAME_LENGTH,
        UserErrorCode.EXCESSIVE_USERNAME_LENGTH.getCodeName()
    );
  }

  private void validateIntroduction(String introduction) {
    checkArgument(
        introduction.length() <= MAX_INTRODUCTION_LENGTH,
        UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH.getCodeName()
    );
  }

  private void validateUrl(String url) {
    checkArgument(
        url.length() <= MAX_URL_LENGTH,
        UserErrorCode.EXCESSIVE_PROFILE_IMAGE_URL_LENGTH.getCodeName()
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
