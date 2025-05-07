package com.ddudu.domain.planning.goal.aggregate.enums;


import static java.util.Objects.isNull;

import com.ddudu.common.exception.GoalErrorCode;
import java.util.Arrays;

public enum GoalStatus {
  IN_PROGRESS,
  DONE;

  public static GoalStatus from(String value) {
    if (isNull(value)) {
      return GoalStatus.IN_PROGRESS;
    }

    return Arrays.stream(GoalStatus.values())
        .filter(status -> value.toUpperCase()
            .equals(status.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(GoalErrorCode.INVALID_GOAL_STATUS.getCodeName()));
  }
}
