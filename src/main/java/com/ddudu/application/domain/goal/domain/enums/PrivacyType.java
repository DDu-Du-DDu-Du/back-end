package com.ddudu.application.domain.goal.domain.enums;

import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import java.util.Arrays;

public enum PrivacyType {
  PRIVATE,
  FOLLOWER,
  PUBLIC;

  public static PrivacyType from(String value) {
    String upperCaseValue = value.toUpperCase();

    return Arrays.stream(PrivacyType.values())
        .filter(providerType -> upperCaseValue.equals(providerType.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(GoalErrorCode.INVALID_PRIVACY_TYPE.getCodeName()));
  }
}
