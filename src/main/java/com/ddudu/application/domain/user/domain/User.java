package com.ddudu.application.domain.user.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;

import com.ddudu.application.domain.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  private static final int MAX_NICKNAME_LENGTH = 20;
  private static final int MAX_OPTIONAL_USERNAME_LENGTH = 20;
  private static final int MAX_INTRODUCTION_LENGTH = 50;

  @EqualsAndHashCode.Include
  private final Long id;
  private final String username;
  private final String nickname;
  private final String introduction;
  private final Authority authority;
  private final UserStatus status;
  private final Options options;

  @Builder
  public User(
      Long id, String username, String nickname, String introduction, Authority authority,
      UserStatus status, Options options
  ) {
    validate(nickname, username, introduction);

    this.id = id;
    this.username = username;
    this.nickname = nickname;
    this.authority = Objects.requireNonNullElse(authority, Authority.NORMAL);
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    this.status = isNull(status) ? UserStatus.ACTIVE : status;
    this.options = isNull(options) ? new Options() : options;
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

  private void validate(String nickname, String optionalUsername, String introduction) {
    validateNickname(nickname);

    if (Objects.nonNull(optionalUsername)) {
      validateUsername(optionalUsername);
    }

    if (Objects.nonNull(introduction)) {
      validateIntroduction(introduction);
    }
  }

  private void validateNickname(String nickname) {
    checkArgument(StringUtils.isBlank(nickname), UserErrorCode.BLANK_NICKNAME.name());
    checkArgument(
        nickname.length() > MAX_NICKNAME_LENGTH, UserErrorCode.EXCESSIVE_NICKNAME_LENGTH.name());
  }

  private void validateUsername(String username) {
    checkArgument(
        StringUtils.isBlank(username), UserErrorCode.BLANK_USERNAME.name());
  }

  private void validateIntroduction(String introduction) {
    checkArgument(
        introduction.length() > MAX_INTRODUCTION_LENGTH,
        UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH.name()
    );
  }

}
