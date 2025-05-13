package com.ddudu.domain.user.auth.aggregate;

import com.ddudu.domain.user.auth.aggregate.vo.UserFamily;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

  private final Long id;

  @Getter(AccessLevel.NONE)
  private final UserFamily userFamily;
  private final String tokenValue;

  @Builder
  private RefreshToken(Long id, String tokenValue, UserFamily userFamily, Long userId, int family) {
    this.id = id;
    this.userFamily = Objects.requireNonNullElseGet(
        userFamily,
        () -> UserFamily.builder()
            .userId(userId)
            .family(family)
            .build()
    );
    this.tokenValue = tokenValue;
  }

  public Long getUserId() {
    return userFamily.getUserId();
  }

  public int getFamily() {
    return userFamily.getFamily();
  }

  public boolean hasSameId(Long id) {
    return Objects.equals(this.id, id);
  }

  public boolean hasSameTokenValue(String tokenValue) {
    return this.tokenValue.equals(tokenValue);
  }

}
