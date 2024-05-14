package com.ddudu.application.domain.authentication.domain;

import com.ddudu.application.domain.authentication.domain.vo.UserFamily;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

  @Getter(AccessLevel.NONE)
  private final UserFamily userFamily;
  private final String tokenValue;

  @Builder
  private RefreshToken(String tokenValue, UserFamily userFamily, String userFamilyValue) {
    this.userFamily = Objects.requireNonNullElseGet(userFamily, () -> UserFamily.builder()
        .userFamilyValue(userFamilyValue)
        .build());
    this.tokenValue = tokenValue;
  }

  public Long getUserId() {
    return userFamily.getUserId();
  }

  public int getFamily() {
    return userFamily.getFamily();
  }

}
