package com.modoo.domain.planning.todo.aggregate.enums;

import java.util.Arrays;
import java.util.Objects;

public enum TodoStatus {
  UNCOMPLETED,
  COMPLETE;

  public static TodoStatus from(String value) {
    return Arrays.stream(TodoStatus.values())
        .filter(status -> Objects.equals(value, status.name()))
        .findFirst()
        .orElse(TodoStatus.UNCOMPLETED);
  }

  public TodoStatus switchStatus() {
    return this == UNCOMPLETED ? COMPLETE : UNCOMPLETED;
  }

  public boolean isCompleted() {
    return this == COMPLETE;
  }

}
