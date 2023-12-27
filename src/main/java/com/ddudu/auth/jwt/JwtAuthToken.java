package com.ddudu.auth.jwt;

import java.util.Collections;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtAuthToken extends JwtAuthenticationToken {

  private final Long userId;

  public JwtAuthToken(Jwt jwt, GrantedAuthority authority, Long userId) {
    super(jwt, Collections.singletonList(authority));
    this.userId = userId;
  }

  public long getUserId() {
    if (Objects.isNull(userId)) {
      throw new IllegalStateException("User is not able to be loaded from this token.");
    }

    return userId;
  }
}