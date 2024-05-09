package com.ddudu.application.domain.authentication.service.converter;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.domain.user.domain.Authority;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthorityConverter implements Converter<Jwt, GrantedAuthority> {

  private static final String AUTHORITY_CLAIM_NAME = "auth";

  @Override
  public GrantedAuthority convert(Jwt jwt) {
    String authorityName = jwt.getClaimAsString(AUTHORITY_CLAIM_NAME);

    try {
      return Authority.valueOf(authorityName);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException(AuthErrorCode.INVALID_TOKEN_AUTHORITY.name());
    }
  }

}
