package com.modoo.domain.user.user.aggregate.enums;

import static com.google.common.base.Preconditions.checkArgument;

import com.modoo.common.exception.UserErrorCode;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum TimeFormat {
  TWELVE_HOUR("12H"),
  TWENTY_FOUR_HOUR("24H");

  private final String code;

  public static TimeFormat get(String input) {
    checkArgument(
        StringUtils.isNotBlank(input),
        UserErrorCode.INVALID_TIME_FORMAT.getCodeName()
    );

    String upperCased = input.toUpperCase(Locale.ROOT);

    return Arrays.stream(values())
        .filter(timeFormat -> timeFormat.code.equals(upperCased))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(
            UserErrorCode.INVALID_TIME_FORMAT.getCodeName()
        ));
  }

}
