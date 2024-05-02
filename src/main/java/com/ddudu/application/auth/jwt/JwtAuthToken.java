package com.ddudu.application.auth.jwt;

import com.ddudu.application.auth.exception.AuthErrorCode;
import com.ddudu.application.common.exception.InvalidTokenException;
import java.util.Collections;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
public class JwtAuthToken extends JwtAuthenticationToken {

  private final Long userId;

  public JwtAuthToken(Jwt jwt, GrantedAuthority authority, Long userId) {
    super(jwt, Collections.singletonList(authority));
    this.userId = userId;
  }

  public long getUserId() {
    if (Objects.isNull(userId)) {
      log.error("JWT ({}) is somehow created without user id", getToken().getTokenValue());
      throw new InvalidTokenException(AuthErrorCode.BAD_TOKEN_CONTENT);
    }

    return userId;
  }

}
