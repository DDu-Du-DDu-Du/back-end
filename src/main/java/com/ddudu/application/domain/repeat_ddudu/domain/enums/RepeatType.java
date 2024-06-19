package com.ddudu.application.domain.repeat_ddudu.domain.enums;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.repeat_ddudu.domain.DailyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.MonthlyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.WeeklyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatPatternRequest;
import java.util.Arrays;

public enum RepeatType {
  DAILY() {
    RepeatPattern createPattern(CreateRepeatPatternRequest request) {
      return new DailyRepeatPattern();
    }
  },
  WEEKLY() {
    RepeatPattern createPattern(CreateRepeatPatternRequest request) {
      return new WeeklyRepeatPattern(request.repeatDaysOfWeek());
    }

  },
  MONTHLY() {
    RepeatPattern createPattern(CreateRepeatPatternRequest request) {
      return new MonthlyRepeatPattern(request.repeatDaysOfMonth(), request.lastDayOfMonth());
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

  public RepeatPattern createRepeatPattern(CreateRepeatPatternRequest request) {
    return createPattern(request);
  }

  abstract RepeatPattern createPattern(CreateRepeatPatternRequest request);
}
