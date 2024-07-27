package com.ddudu.application.domain.period_goal.domain.enums;

import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import java.util.Arrays;

public enum PeriodGoalType {
  MONTH,
  WEEK;

  public static PeriodGoalType from(String value) {
    return Arrays.stream(PeriodGoalType.values())
        .filter(status -> value.toUpperCase()
            .equals(status.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(GoalErrorCode.INVALID_GOAL_STATUS.getCodeName()));
  }
}
