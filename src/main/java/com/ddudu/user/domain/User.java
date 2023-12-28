package com.ddudu.user.domain;

import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.exception.UserErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "optional_username", length = 20, unique = true)
  private String optionalUsername;

  @Embedded
  @AttributeOverride(
      name = "address",
      column = @Column(name = "email", length = 50, nullable = false, unique = true)
  )
  private Email email;

  @Embedded
  @AttributeOverride(name = "encrypted", column = @Column(name = "password", nullable = false))
  private Password password;

  @Column(name = "nickname", length = 20, nullable = false)
  private String nickname;

  @Column(name = "introduction", length = 50)
  private String introduction;

  @Column(name = "status", columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
  @CreatedDate
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
  @LastModifiedDate
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime updatedAt;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  @Builder
  public User(
      String optionalUsername, String email, String password, PasswordEncoder passwordEncoder,
      String nickname, String introduction
  ) {
    validate(nickname, optionalUsername, introduction);
    this.optionalUsername = optionalUsername;
    this.email = new Email(email);
    this.password = new Password(password, passwordEncoder);
    this.nickname = nickname;
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    status = UserStatus.ACTIVE;
    isDeleted = false;
  }

  public String getEmail() {
    return email.getAddress();
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

    if (nickname.length() > 20) {
      throw new InvalidParameterException(UserErrorCode.EXCESSIVE_NICKNAME_LENGTH);
    }
  }

  private void validateOptionalUsername(String optionalUsername) {
    if (StringUtils.isBlank(optionalUsername)) {
      throw new InvalidParameterException(UserErrorCode.BLANK_OPTIONAL_USERNAME);
    }

    if (optionalUsername.length() > 20) {
      throw new InvalidParameterException(UserErrorCode.EXCESSIVE_OPTIONAL_USERNAME_LENGTH);
    }
  }

  private void validateIntroduction(String introduction) {
    if (introduction.length() > 50) {
      throw new InvalidParameterException(UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH);
    }
  }

}
