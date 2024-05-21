package com.ddudu.application.domain.goal.domain.vo;

import static com.google.common.base.Preconditions.checkArgument;
import static io.micrometer.common.util.StringUtils.isBlank;

import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class Color {

  private static final int HEX_COLOR_CODE_LENGTH = 6;
  private static final Pattern HEX_COLOR_CODE_PATTERN = Pattern.compile(
      "^[0-9A-Fa-f]{" + HEX_COLOR_CODE_LENGTH + "}$");
  private static final String DEFAULT_COLOR_CODE = "191919";
  private final String code;

  public Color(String code) {
    this.code = confirmCode(code);
  }

  private String confirmCode(String code) {
    if (isBlank(code)) {
      return DEFAULT_COLOR_CODE;
    }

    validate(code);

    return code;
  }

  private void validate(String code) {
    checkArgument(HEX_COLOR_CODE_PATTERN.matcher(code)
        .matches(), GoalErrorCode.INVALID_COLOR_FORMAT.getCodeName());
  }

}
