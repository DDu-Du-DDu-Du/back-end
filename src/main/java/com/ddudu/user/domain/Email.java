package com.ddudu.user.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Email {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+[.][0-9A-Za-z]+$");

  private String address;

  public Email(String address) {
    validate(address);
    this.address = address;
  }

  private void validate(String email) {
    if (StringUtils.isBlank(email)) {
      throw new IllegalArgumentException("이메일이 입력되지 않았습니다.");
    }

    boolean matches = EMAIL_PATTERN.matcher(email)
        .matches();

    if (!matches) {
      throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
    }
  }

}
