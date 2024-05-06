package com.ddudu.application.domain.user.domain;

import com.ddudu.presentation.api.exception.InvalidParameterException;
import com.ddudu.old.user.exception.UserErrorCode;
import io.micrometer.common.util.StringUtils;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class Email {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+[.][0-9A-Za-z]+$");

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

  public static boolean isValidEmail(String email) {
    if (StringUtils.isBlank(email)) {
      return false;
    }
    return EMAIL_PATTERN.matcher(email)
        .matches();
  }

}
