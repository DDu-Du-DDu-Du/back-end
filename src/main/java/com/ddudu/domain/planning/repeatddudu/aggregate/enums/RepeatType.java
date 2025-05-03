package com.ddudu.domain.planning.repeatddudu.aggregate.enums;

import static java.util.Objects.isNull;

import com.ddudu.domain.planning.repeatddudu.aggregate.DailyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.MonthlyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.WeeklyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.planning.repeatddudu.dto.RepeatPatternDto;
import java.util.Arrays;
import java.util.function.Function;

public enum RepeatType {
  DAILY(request -> new DailyRepeatPattern()),
  WEEKLY(request -> new WeeklyRepeatPattern(request.repeatDaysOfWeek())),
  MONTHLY(request -> new MonthlyRepeatPattern(
      request.repeatDaysOfMonth(),
      request.lastDayOfMonth()
  ));

  private final Function<RepeatPatternDto, RepeatPattern> createPattern;

  RepeatType(Function<RepeatPatternDto, RepeatPattern> createPattern) {
    this.createPattern = createPattern;
  }

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
    return createPattern.apply(request);
  }
}
