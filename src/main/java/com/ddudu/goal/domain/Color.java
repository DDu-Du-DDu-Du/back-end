package com.ddudu.goal.domain;

import static io.micrometer.common.util.StringUtils.isBlank;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Color {

  private static final int HEX_COLOR_CODE_LENGTH = 6;
  private static final Pattern HEX_COLOR_CODE_PATTERN = Pattern.compile(
      "^[0-9A-Fa-f]{" + HEX_COLOR_CODE_LENGTH + "}$");
  private static final String DEFAULT_COLOR_CODE = "191919";

  private String code;

  public Color(String code) {
    validate(code);
    this.code = isBlank(code) ? DEFAULT_COLOR_CODE : code;
  }

  private void validate(String code) {
    if (isBlank(code)) {
      return;
    }

    boolean matches = HEX_COLOR_CODE_PATTERN.matcher(code)
        .matches();

    if (!matches) {
      throw new IllegalArgumentException("올바르지 않은 색상 코드입니다. 색상 코드는 "
          + HEX_COLOR_CODE_LENGTH + "자리 16진수입니다.");
    }
  }

}
