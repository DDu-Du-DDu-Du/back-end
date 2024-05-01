package com.ddudu.user.domain;

import static java.util.Objects.isNull;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.domain.BaseDomain;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class User extends BaseDomain {

  private static final int MAX_NICKNAME_LENGTH = 20;
  private static final int MAX_OPTIONAL_USERNAME_LENGTH = 20;
  private static final int MAX_INTRODUCTION_LENGTH = 50;

  private Long id;
  private String optionalUsername;
  private Email email;
  private Password password;
  private String nickname;
  private String introduction;
  private Authority authority;
  private UserStatus status;
  private Options options;

  @Builder
  public User(
      Long id, String optionalUsername, String email, Password password,
      String nickname, String introduction, Authority authority, UserStatus status, Options options,
      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isDeleted
  ) {
    super(createdAt, updatedAt, isDeleted);
    validate(nickname, optionalUsername, introduction);

    this.id = id;
    this.optionalUsername = optionalUsername;
    this.email = new Email(email);
    this.password = password;
    this.nickname = nickname;
    this.authority = Objects.requireNonNullElse(authority, Authority.NORMAL);
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    this.status = isNull(status) ? UserStatus.ACTIVE : status;
    this.options = isNull(options) ? new Options() : options;
  }

  public String getEmail() {
    return email.getAddress();
  }

  public boolean isSameEmail(String newEmail) {
    return email.isSame(newEmail);
  }

  public void applyEmailUpdate(String newEmail) {
    email = new Email(newEmail);
  }

  public void applyPasswordUpdate(String newPassword, PasswordEncoder passwordEncoder) {
    if (password.check(newPassword, passwordEncoder)) {
      throw new DuplicateResourceException(UserErrorCode.DUPLICATE_EXISTING_PASSWORD);
    }

    password = new Password(newPassword, passwordEncoder);
  }

  public void applyProfileUpdate(String nickname, String introduction) {
    validate(nickname, null, introduction);

    this.nickname = nickname;
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
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
