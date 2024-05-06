package com.ddudu.application.domain.user.domain;

import com.ddudu.application.domain.user.exception.UserErrorCode;
import com.ddudu.presentation.api.exception.InvalidParameterException;
import io.micrometer.common.util.StringUtils;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class Password {

  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
      "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&^]).{8,50}$");
  private static final int MIN_PASSWORD_LENGTH = 8;

  private String encrypted;

  public Password(String encrypted) {
    this.encrypted = encrypted;
  }

  public Password(String rawPassword, PasswordEncoder passwordEncoder) {
    validate(rawPassword);
    encrypted = passwordEncoder.encode(rawPassword);
  }

  public boolean check(String rawPassword, PasswordEncoder passwordEncoder) {
    return passwordEncoder.matches(rawPassword, encrypted);
  }

  private void validate(String password) {
    if (StringUtils.isBlank(password)) {
      throw new InvalidParameterException(UserErrorCode.BLANK_PASSWORD);
    }

    if (password.length() < MIN_PASSWORD_LENGTH) {
      throw new InvalidParameterException(UserErrorCode.INSUFFICIENT_PASSWORD_LENGTH);
    }

    boolean matches = PASSWORD_PATTERN.matcher(password)
        .matches();

    if (!matches) {
      throw new InvalidParameterException(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }
  }

}
