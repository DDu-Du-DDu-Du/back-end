package com.ddudu.application.domain.authentication.domain.vo;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserFamily {

  private final Long userId;
  private final int family;

  @Builder
  private UserFamily(String userFamilyValue) {
    String[] userFamily = userFamilyValue.split(" ");

    checkArgument(
        userFamily.length == 2, AuthErrorCode.UNABLE_TO_PARSE_USER_FAMILY_VALUE.getCodeName());

    this.userId = getUserId(userFamily[0]);
    this.family = getFamily(userFamily[1]);
  }

  private Long getUserId(String userIdValue) {
    try {
      return Long.parseLong(userIdValue);
    } catch (RuntimeException e) {
      throw new IllegalArgumentException(
          AuthErrorCode.INVALID_USER_ID_FOR_REFRESH_TOKEN.getCodeName());
    }
  }

  private int getFamily(String familyValue) {
    try {
      return Integer.parseInt(familyValue);
    } catch (RuntimeException e) {
      throw new IllegalArgumentException(AuthErrorCode.INVALID_REFRESH_TOKEN_FAMILY.getCodeName());
    }
  }

}
