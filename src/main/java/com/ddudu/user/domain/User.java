package com.ddudu.user.domain;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.BaseEntity;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
@SQLRestriction("is_deleted = 0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

  private static final int MAX_NICKNAME_LENGTH = 20;
  private static final int MAX_OPTIONAL_USERNAME_LENGTH = 20;
  private static final int MAX_INTRODUCTION_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "optional_username", length = 20, unique = true)
  private String optionalUsername;

  @Embedded
  private Email email;

  @Embedded
  private Password password;

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

  @Embedded
  private Options options;

  @Builder
  public User(
      String optionalUsername, String email, String password, PasswordEncoder passwordEncoder,
      String nickname, String introduction, Authority authority
  ) {
    validate(nickname, optionalUsername, introduction);

    this.optionalUsername = optionalUsername;
    this.email = new Email(email);
    this.password = new Password(password, passwordEncoder);
    this.nickname = nickname;
    this.authority = Objects.requireNonNullElse(authority, Authority.NORMAL);
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    options = new Options();
    status = UserStatus.ACTIVE;
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
