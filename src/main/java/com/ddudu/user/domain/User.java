package com.ddudu.user.domain;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.BaseEntity;
import com.ddudu.auth.domain.authority.Authority;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

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

  @Column(name = "authority", columnDefinition = "VARCHAR", length = 15)
  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(name = "status", columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

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
    this.authority = authority != null ? authority : Authority.NORMAL;
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    this.authority = authority != null ? authority : Authority.NORMAL;
    status = UserStatus.ACTIVE;
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
      throw new IllegalArgumentException("닉네임이 입력되지 않았습니다.");
    }

    if (nickname.length() > 20) {
      throw new IllegalArgumentException("닉네임은 최대 20자 입니다.");
    }
  }

  private void validateOptionalUsername(String optionalUsername) {
    if (StringUtils.isBlank(optionalUsername)) {
      throw new IllegalArgumentException("아이디는 공백일 수 없습니다.");
    }

    if (optionalUsername.length() > 20) {
      throw new IllegalArgumentException("아이디는 최대 20자 입니다.");
    }
  }

  private void validateIntroduction(String introduction) {
    if (introduction.length() > 50) {
      throw new IllegalArgumentException("자기소개는 최대 50자 입니다.");
    }
  }

}
