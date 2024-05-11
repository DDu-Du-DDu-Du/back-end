package com.ddudu.application.domain.authentication.domain.vo;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import lombok.Getter;

@Getter
public class HashedToken {

  private final String tokenValue;

  public HashedToken(String rawToken) {
    this.tokenValue = Hashing.sha256()
        .hashString(rawToken, StandardCharsets.UTF_8)
        .toString();
  }

}
