package com.ddudu.application.domain.period_goal.domain.enums;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import java.util.Arrays;

public enum PeriodGoalType {
  MONTH,
  WEEK;

  public static PeriodGoalType from(String value) {
    validateType(value);

    return Arrays.stream(PeriodGoalType.values())
        .filter(status -> value.toUpperCase()
            .equals(status.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(GoalErrorCode.INVALID_GOAL_STATUS.getCodeName()));
  }

  private static void validateType(String type) {
    checkArgument(nonNull(type), PeriodGoalErrorCode.PERIOD_GOAL_TYPE_NOT_EXISTING.getCodeName());
  }
}
