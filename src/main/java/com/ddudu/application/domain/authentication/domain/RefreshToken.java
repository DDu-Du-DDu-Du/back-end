package com.ddudu.application.domain.authentication.domain;

import static com.google.common.base.Preconditions.checkState;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

  private final Long userId;
  private final int family;
  private final String tokenValue;

  @Builder
  private RefreshToken(String tokenValue, Long userId, Integer family) {
    checkState(Objects.nonNull(userId), AuthErrorCode.NULL_USER_ID_FOR_REFRESH_TOKEN.getCodeName());
    checkState(Objects.nonNull(family), AuthErrorCode.NULL_USER_ID_FOR_REFRESH_TOKEN.getCodeName());

    this.userId = userId;
    this.tokenValue = tokenValue;
    this.family = family;
  }

}
