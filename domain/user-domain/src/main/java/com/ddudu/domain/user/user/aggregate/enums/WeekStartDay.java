package com.ddudu.domain.user.user.aggregate.enums;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.common.exception.UserErrorCode;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public enum WeekStartDay {
  SUN,
  MON;

  public static WeekStartDay get(String input) {
    checkArgument(
        StringUtils.isNotBlank(input),
        UserErrorCode.INVALID_WEEK_START_DAY.getCodeName()
    );

    String upperCased = input.toUpperCase(Locale.ROOT);

    return Arrays.stream(values())
        .filter(weekStartDay -> weekStartDay.name().equals(upperCased))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(
            UserErrorCode.INVALID_WEEK_START_DAY.getCodeName()
        ));
  }

}
