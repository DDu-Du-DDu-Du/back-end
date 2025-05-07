package com.ddudu.domain.planning.repeatddudu.aggregate.enums;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import com.ddudu.domain.planning.repeatddudu.aggregate.vo.DailyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.MonthlyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.RepeatInfo;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.RepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.WeeklyRepeatPattern;
import com.ddudu.common.exception.RepeatDduduErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RepeatType {
  DAILY(request -> new DailyRepeatPattern()),
  WEEKLY(request -> new WeeklyRepeatPattern(request.repeatDaysOfWeek())),
  MONTHLY(request -> new MonthlyRepeatPattern(
      request.repeatDaysOfMonth(),
      request.lastDayOfMonth()
  ));

  private final Function<RepeatInfo, RepeatPattern> createPattern;

  public static RepeatType from(String value) {
    checkArgument(nonNull(value), RepeatDduduErrorCode.NULL_REPEAT_TYPE.getCodeName());

    return Arrays.stream(RepeatType.values())
        .filter(type -> value.toUpperCase()
            .equals(type.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(
                RepeatDduduErrorCode.INVALID_REPEAT_TYPE.getCodeName()));
  }

  public RepeatPattern createRepeatPattern(
      List<String> repeatDaysOfWeek,
      List<Integer> repeatDaysOfMonth,
      Boolean lastDayOfMonth
  ) {
    RepeatInfo repeatInfo = new RepeatInfo(repeatDaysOfWeek, repeatDaysOfMonth, lastDayOfMonth);

    return createPattern.apply(repeatInfo);
  }
}
