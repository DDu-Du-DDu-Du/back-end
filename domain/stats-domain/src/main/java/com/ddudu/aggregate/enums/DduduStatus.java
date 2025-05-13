package com.ddudu.aggregate.enums;

import java.util.Arrays;
import java.util.Objects;

public enum DduduStatus {
  UNCOMPLETED,
  COMPLETE;

  public static DduduStatus from(String value) {
    return Arrays.stream(DduduStatus.values())
        .filter(status -> Objects.equals(value, status.name()))
        .findFirst()
        .orElseGet(() -> DduduStatus.UNCOMPLETED);
  }

  public boolean isCompleted() {
    return this == COMPLETE;
  }

}
