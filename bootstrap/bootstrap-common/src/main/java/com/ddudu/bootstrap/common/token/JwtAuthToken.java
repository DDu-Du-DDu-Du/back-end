package com.ddudu.bootstrap.common.token;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.domain.user.auth.exception.AuthErrorCode;
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
    checkArgument(Objects.nonNull(userId), AuthErrorCode.BAD_TOKEN_CONTENT.getCodeName());

    return userId;
  }

}
