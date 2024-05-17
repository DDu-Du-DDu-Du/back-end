package com.ddudu.application.domain.goal.domain.enums;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import java.util.Arrays;

public enum PrivacyType {
  PRIVATE,
  FOLLOWER,
  PUBLIC;

  public static PrivacyType from(String value) {
    if (isNull(value)) {
      return PrivacyType.PRIVATE;
    }

    return Arrays.stream(PrivacyType.values())
        .filter(providerType -> value.toUpperCase()
            .equals(providerType.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(GoalErrorCode.INVALID_PRIVACY_TYPE.getCodeName()));
  }
}
