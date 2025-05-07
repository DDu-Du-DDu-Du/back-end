package com.ddudu.api.user.auth.jwt.converter;

import com.ddudu.api.user.auth.jwt.AuthorityProxy;
import com.ddudu.common.exception.AuthErrorCode;
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
      return AuthorityProxy.valueOf(authorityName);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException(AuthErrorCode.INVALID_TOKEN_AUTHORITY.getCodeName());
    }
  }

}
