package com.ddudu.old.auth.jwt.converter;

import com.ddudu.application.domain.user.domain.Authority;
import com.ddudu.old.auth.exception.AuthErrorCode;
import com.ddudu.presentation.api.exception.InvalidTokenException;
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
      throw new InvalidTokenException(AuthErrorCode.INVALID_TOKEN_AUTHORITY);
    }
  }

}
