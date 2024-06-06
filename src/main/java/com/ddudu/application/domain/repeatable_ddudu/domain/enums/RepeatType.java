package com.ddudu.application.domain.repeatable_ddudu.domain.enums;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.repeatable_ddudu.exception.RepeatableDduduErrorCode;
import java.util.Arrays;

public enum RepeatType {
  DAILY,
  WEEKLY,
  MONTHLY;

  public static RepeatType from(String value) {
    if (isNull(value)) {
      return null;
    }

    return Arrays.stream(RepeatType.values())
        .filter(status -> value.toUpperCase()
            .equals(status.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(
                RepeatableDduduErrorCode.INVALID_REPEAT_TYPE.getCodeName()));
  }
}
