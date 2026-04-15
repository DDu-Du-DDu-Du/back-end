package com.modoo.aggregate.enums;

import com.modoo.common.exception.StatsErrorCode;
import java.util.Arrays;
import java.util.Objects;

public enum TodoStatus {
  UNCOMPLETED,
  COMPLETE;

  public static TodoStatus from(String value) {
    return Arrays.stream(TodoStatus.values())
        .filter(status -> Objects.equals(value, status.name()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(StatsErrorCode.INVALID_TODO_STATUS.getCodeName()));
  }

  public boolean isCompleted() {
    return this == COMPLETE;
  }

}
