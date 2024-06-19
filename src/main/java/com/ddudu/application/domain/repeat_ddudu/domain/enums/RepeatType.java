package com.ddudu.application.domain.repeat_ddudu.domain.enums;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.repeat_ddudu.domain.DailyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.MonthlyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.WeeklyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.dto.repeat_ddudu.RepeatPatternDto;
import java.util.Arrays;

public enum RepeatType {
  DAILY() {
    RepeatPattern createPattern(RepeatPatternDto request) {
      return new DailyRepeatPattern();
    }
  },
  WEEKLY() {
    RepeatPattern createPattern(RepeatPatternDto request) {
      return WeeklyRepeatPattern.withValidation(request.repeatDaysOfWeek());
    }

  },
  MONTHLY() {
    RepeatPattern createPattern(RepeatPatternDto request) {
      return MonthlyRepeatPattern.withValidation(
          request.repeatDaysOfMonth(), request.lastDayOfMonth());
    }
  };

  public static RepeatType from(String value) {
    if (isNull(value)) {
      return null;
    }

    return Arrays.stream(RepeatType.values())
        .filter(type -> value.toUpperCase()
            .equals(type.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(
                RepeatDduduErrorCode.INVALID_REPEAT_TYPE.getCodeName()));
  }

  public RepeatPattern createRepeatPattern(RepeatPatternDto request) {
    return createPattern(request);
  }

  abstract RepeatPattern createPattern(RepeatPatternDto request);
}
