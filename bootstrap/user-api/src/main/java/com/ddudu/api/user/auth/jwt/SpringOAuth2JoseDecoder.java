package com.ddudu.api.user.auth.jwt;

import com.ddudu.application.port.auth.in.DduduJwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringOAuth2JoseDecoder implements DduduJwtDecoder {

  private final JwtDecoder jwtDecoder;

  @Override
  public String getSub(String token) {
    Jwt jwt = jwtDecoder.decode(token);

    return jwt.getClaimAsString(JwtClaimNames.SUB);
  }

}
