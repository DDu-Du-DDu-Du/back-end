package com.ddudu.bootstrap.common.token.converter;

import com.ddudu.bootstrap.common.token.JwtAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private static final String USER_PRINCIPAL_NAME = "user";

  private final JwtAuthorityConverter jwtAuthorityConverter;

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    GrantedAuthority authority = jwtAuthorityConverter.convert(jwt);
    String userIdClaim = jwt.getClaimAsString(USER_PRINCIPAL_NAME);
    Long userId = Long.parseLong(userIdClaim);

    return new JwtAuthToken(jwt, authority, userId);
  }

}
