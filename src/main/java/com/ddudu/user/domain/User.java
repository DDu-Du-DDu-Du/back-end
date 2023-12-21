package com.ddudu.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
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
import java.util.regex.Pattern;
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

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+[.][0-9A-Za-z]+$");
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
      "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&^]).{8,50}$");

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "optional_username", length = 20, unique = true)
  private String optionalUsername;

  @Column(name = "email", length = 50, nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "nickname", length = 30, nullable = false)
  private String nickname;

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

  @Column(name = "is_deleted", nullable = false, columnDefinition = "TINYINT")
  private boolean isDeleted;

  @Builder
  public User(
      String optionalUsername, String email, String password, PasswordEncoder passwordEncoder,
      String nickname
  ) {
    validate(email, password, nickname, optionalUsername);
    this.optionalUsername = optionalUsername;
    this.email = email;
    this.password = encodePassword(password, passwordEncoder);
    this.nickname = nickname;
    status = UserStatus.ACTIVE;
    isDeleted = false;
  }

  private void validate(String email, String password, String nickname, String optionalUsername) {
    validateEmail(email);
    validatePassword(password);
    validateNickname(nickname);
    validateOptionalUsername(optionalUsername);
  }

  private void validateEmail(String email) {
    if (StringUtils.isBlank(email)) {
      throw new IllegalArgumentException("이메일이 입력되지 않았습니다.");
    }

    boolean matches = EMAIL_PATTERN.matcher(email)
        .matches();

    if (!matches) {
      throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
    }
  }

  private void validatePassword(String password) {
    if (StringUtils.isBlank(password)) {
      throw new IllegalArgumentException("비밀번호가 입력되지 않았습니다.");
    }

    if (password.length() < 8) {
      throw new IllegalArgumentException("비밀번호는 8자리 이상이어야 합니다.");
    }

    boolean matches = PASSWORD_PATTERN.matcher(password)
        .matches();

    if (!matches) {
      throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자로 구성되어야 합니다.");
    }
  }

  private void validateNickname(String nickname) {
    if (StringUtils.isBlank(nickname)) {
      throw new IllegalArgumentException("닉네임이 입력되지 않았습니다.");
    }

    if (nickname.length() > 20) {
      throw new IllegalArgumentException("닉네임은 최대 20자 입니다.");
    }
  }

  private void validateOptionalUsername(String optionalUsername) {
    if (Objects.nonNull(optionalUsername) && StringUtils.isBlank(optionalUsername)) {
      throw new IllegalArgumentException("아이디는 공백일 수 없습니다.");
    }

    if (optionalUsername.length() > 20) {
      throw new IllegalArgumentException("아이디는 최대 20자 입니다.");
    }
  }

  private String encodePassword(String password, PasswordEncoder passwordEncoder) {
    return passwordEncoder.encode(password);
  }

}
