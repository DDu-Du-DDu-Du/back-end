package com.ddudu.user.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
      "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&^]).{8,50}$");
  private static final int MIN_PASSWORD_LENGTH = 8;

  private String encrypted;

  public Password(String rawPassword, PasswordEncoder passwordEncoder) {
    validate(rawPassword);
    encrypted = passwordEncoder.encode(rawPassword);
  }

  private void validate(String password) {
    if (StringUtils.isBlank(password)) {
      throw new IllegalArgumentException("비밀번호가 입력되지 않았습니다.");
    }

    if (password.length() < MIN_PASSWORD_LENGTH) {
      throw new IllegalArgumentException("비밀번호는 8자리 이상이어야 합니다.");
    }

    boolean matches = PASSWORD_PATTERN.matcher(password)
        .matches();

    if (!matches) {
      throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자로 구성되어야 합니다.");
    }
  }

}
