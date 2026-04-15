package com.modoo.domain.planning.repeattodo.aggregate.enums;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import com.modoo.common.exception.RepeatTodoErrorCode;
import com.modoo.domain.planning.repeattodo.aggregate.vo.DailyRepeatPattern;
import com.modoo.domain.planning.repeattodo.aggregate.vo.MonthlyRepeatPattern;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatInfo;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatPattern;
import com.modoo.domain.planning.repeattodo.aggregate.vo.WeeklyRepeatPattern;
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
    checkArgument(nonNull(value), RepeatTodoErrorCode.NULL_REPEAT_TYPE.getCodeName());

    return Arrays.stream(RepeatType.values())
        .filter(type -> value.toUpperCase()
            .equals(type.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(
                RepeatTodoErrorCode.INVALID_REPEAT_TYPE.getCodeName()));
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
