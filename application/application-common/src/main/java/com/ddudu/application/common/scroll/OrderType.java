package com.ddudu.application.common.scroll;

import java.util.Arrays;
import java.util.Objects;

public enum OrderType {
  LATEST;

  public static OrderType from(String value) {
    if (Objects.isNull(value)) {
      return LATEST;
    }
    
    String upperValue = value.toUpperCase();

    return Arrays.stream(OrderType.values())
        .filter(type -> upperValue.equals(type.name()))
        .findFirst()
        .orElse(LATEST);
  }

  public boolean isLatest() {
    return this == LATEST;
  }
}
