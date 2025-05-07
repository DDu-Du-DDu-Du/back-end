package com.ddudu.application.dto.repeatddudu;

import com.ddudu.application.dto.goal.request.CreateRepeatDduduRequestWithoutGoal;
import com.ddudu.application.dto.repeatddudu.request.CreateRepeatDduduRequest;
import java.util.List;

public record RepeatPatternDto(
    List<String> repeatDaysOfWeek,
    List<Integer> repeatDaysOfMonth,
    Boolean lastDayOfMonth
) {

  public static RepeatPatternDto from(CreateRepeatDduduRequest request) {
    return new RepeatPatternDto(
        request.repeatDaysOfWeek(),
        request.repeatDaysOfMonth(),
        request.lastDayOfMonth()
    );
  }

  public static RepeatPatternDto from(CreateRepeatDduduRequestWithoutGoal request) {
    return new RepeatPatternDto(
        request.repeatDaysOfWeek(),
        request.repeatDaysOfMonth(),
        request.lastDayOfMonth()
    );
  }

  public static RepeatPatternDto dailyPatternOf() {
    return new RepeatPatternDto(
        null,
        null,
        null
    );
  }

  public static RepeatPatternDto weeklyPatternOf(List<String> repeatDaysOfWeek) {
    return new RepeatPatternDto(
        repeatDaysOfWeek,
        null,
        null
    );
  }

  public static RepeatPatternDto monthlyPatternOf(
      List<Integer> repeatDaysOfMonth, Boolean lastDayOfMonth
  ) {
    return new RepeatPatternDto(
        null,
        repeatDaysOfMonth,
        lastDayOfMonth
    );
  }

  public static RepeatPatternDto monthlyPatternOf(
      List<Integer> repeatDaysOfMonth
  ) {
    return monthlyPatternOf(repeatDaysOfMonth, null);
  }

}
