package com.modoo.domain.user.auth.aggregate;

import com.modoo.domain.user.auth.aggregate.vo.UserFamily;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

  private final Long id;

  @Getter(AccessLevel.NONE)
  private final UserFamily userFamily;
  private final String currentToken;
  private final String previousToken;
  private final LocalDateTime refreshedAt;

  @Builder
  private RefreshToken(
      Long id,
      String tokenValue,
      String currentToken,
      String previousToken,
      LocalDateTime refreshedAt,
      UserFamily userFamily,
      Long userId,
      int family
  ) {
    this.id = id;
    this.userFamily = Objects.requireNonNullElseGet(
        userFamily,
        () -> UserFamily.builder()
            .userId(userId)
            .family(family)
            .build()
    );
    this.currentToken = Objects.requireNonNullElse(currentToken, tokenValue);
    this.previousToken = previousToken;
    this.refreshedAt = refreshedAt;
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
    return this.currentToken.equals(tokenValue);
  }

  public boolean hasSameCurrentToken(String tokenValue) {
    return this.currentToken.equals(tokenValue);
  }

  public boolean hasSamePreviousToken(String tokenValue) {
    return Objects.equals(this.previousToken, tokenValue);
  }

  public boolean isRefreshedWithin(LocalDateTime comparedAt, long minutes) {
    if (Objects.isNull(refreshedAt)) {
      return false;
    }

    return !refreshedAt.plusMinutes(minutes)
        .isBefore(comparedAt);
  }

  public String getTokenValue() {
    return currentToken;
  }

}
