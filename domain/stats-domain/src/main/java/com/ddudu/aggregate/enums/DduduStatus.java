package com.ddudu.aggregate.enums;

import com.ddudu.common.exception.StatsErrorCode;
import java.util.Arrays;
import java.util.Objects;

public enum DduduStatus {
  UNCOMPLETED,
  COMPLETE;

  public static DduduStatus from(String value) {
    return Arrays.stream(DduduStatus.values())
        .filter(status -> Objects.equals(value, status.name()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(StatsErrorCode.INVALID_DDUDU_STATUS.getCodeName()));
  }

  public boolean isCompleted() {
    return this == COMPLETE;
  }

}
