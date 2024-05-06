package com.ddudu.application.domain.user.domain;

import static java.util.Objects.isNull;

import com.ddudu.old.auth.domain.authority.Authority;
import com.ddudu.old.user.exception.UserErrorCode;
import com.ddudu.presentation.api.exception.InvalidParameterException;
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
  private final String optionalUsername;
  private final String nickname;
  private final String introduction;
  private final Authority authority;
  private final UserStatus status;
  private final Options options;

  @Builder
  public User(
      Long id, String optionalUsername, String nickname, String introduction, Authority authority,
      UserStatus status, Options options
  ) {
    validate(nickname, optionalUsername, introduction);

    this.id = id;
    this.optionalUsername = optionalUsername;
    this.nickname = nickname;
    this.authority = Objects.requireNonNullElse(authority, Authority.NORMAL);
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    this.status = isNull(status) ? UserStatus.ACTIVE : status;
    this.options = isNull(options) ? new Options() : options;
  }

  public User applyProfileUpdate(String nickname, String introduction) {
    return User.builder()
        .id(this.id)
        .optionalUsername(this.optionalUsername)
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
      validateOptionalUsername(optionalUsername);
    }

    if (Objects.nonNull(introduction)) {
      validateIntroduction(introduction);
    }
  }

  private void validateNickname(String nickname) {
    if (StringUtils.isBlank(nickname)) {
      throw new InvalidParameterException(UserErrorCode.BLANK_NICKNAME);
    }

    if (nickname.length() > MAX_NICKNAME_LENGTH) {
      throw new InvalidParameterException(UserErrorCode.EXCESSIVE_NICKNAME_LENGTH);
    }
  }

  private void validateOptionalUsername(String optionalUsername) {
    if (StringUtils.isBlank(optionalUsername)) {
      throw new InvalidParameterException(UserErrorCode.BLANK_OPTIONAL_USERNAME);
    }

    if (optionalUsername.length() > MAX_OPTIONAL_USERNAME_LENGTH) {
      throw new InvalidParameterException(UserErrorCode.EXCESSIVE_OPTIONAL_USERNAME_LENGTH);
    }
  }

  private void validateIntroduction(String introduction) {
    if (introduction.length() > MAX_INTRODUCTION_LENGTH) {
      throw new InvalidParameterException(UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH);
    }
  }

}
