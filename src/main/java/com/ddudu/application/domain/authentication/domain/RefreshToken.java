package com.ddudu.application.domain.authentication.domain;

import static com.google.common.base.Preconditions.checkState;

import com.ddudu.application.domain.authentication.domain.vo.HashedToken;
import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

  @Getter(AccessLevel.NONE)
  private final HashedToken hashedToken;
  private final Long userId;
  private final int family;

  @Builder
  private RefreshToken(HashedToken hashedToken, String tokenValue, Long userId, Integer family) {
    checkState(Objects.nonNull(userId), AuthErrorCode.NULL_USER_ID_FOR_REFRESH_TOKEN.getCodeName());
    checkState(Objects.nonNull(family), AuthErrorCode.NULL_USER_ID_FOR_REFRESH_TOKEN.getCodeName());

    this.hashedToken = Objects.requireNonNullElseGet(
        hashedToken, () -> new HashedToken(tokenValue));
    this.userId = userId;
    this.family = family;
  }

  public String getHashedToken() {
    return hashedToken.getTokenValue();
  }

}
