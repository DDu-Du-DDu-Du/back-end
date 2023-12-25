package com.ddudu.user.domain;

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
      String nickname
  ) {
    validate(nickname, optionalUsername);
    this.optionalUsername = optionalUsername;
    this.email = new Email(email);
    this.password = new Password(password, passwordEncoder);
    this.nickname = nickname;
    status = UserStatus.ACTIVE;
    isDeleted = false;
  }

  public String getEmail() {
    return email.getAddress();
  }

  private void validate(String nickname, String optionalUsername) {
    validateNickname(nickname);
    validateOptionalUsername(optionalUsername);
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
    if (Objects.isNull(optionalUsername)) {
      return;
    }

    if (StringUtils.isBlank(optionalUsername)) {
      throw new IllegalArgumentException("아이디는 공백일 수 없습니다.");
    }

    if (optionalUsername.length() > 20) {
      throw new IllegalArgumentException("아이디는 최대 20자 입니다.");
    }
  }

}