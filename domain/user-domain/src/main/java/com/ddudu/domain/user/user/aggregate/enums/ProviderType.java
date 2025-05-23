package com.ddudu.domain.user.user.aggregate.enums;

import com.ddudu.common.exception.UserErrorCode;
import java.util.Arrays;

public enum ProviderType {
  KAKAO;

  public static ProviderType from(String value) {
    return Arrays.stream(ProviderType.values())
        .filter(providerType -> value.equals(providerType.name()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(UserErrorCode.INVALID_PROVIDER_TYPE.getCodeName()));
  }
}
