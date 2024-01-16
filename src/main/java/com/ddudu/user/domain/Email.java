package com.ddudu.user.domain;

import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
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

  @Column(name = "email", length = 50, nullable = false, unique = true)
  private String address;

  public Email(String address) {
    validate(address);
    this.address = address;
  }

  public boolean isSame(String email) {
    return address.equals(email);
  }

  private void validate(String email) {
    if (StringUtils.isBlank(email)) {
      throw new InvalidParameterException(UserErrorCode.BLANK_EMAIL);
    }

    boolean matches = EMAIL_PATTERN.matcher(email)
        .matches();

    if (!matches) {
      throw new InvalidParameterException(UserErrorCode.INVALID_EMAIL_FORMAT);
    }
  }

}
