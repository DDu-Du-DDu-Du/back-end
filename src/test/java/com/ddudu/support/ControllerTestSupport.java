package com.ddudu.support;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@Import({WebSecurityConfig.class, TestProperties.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
public class ControllerTestSupport extends RestDocsSupport {

  protected static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
      .build();
  protected static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
      .claim("auth", Authority.NORMAL);
  protected static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;

  @Autowired
  protected JwtEncoder jwtEncoder;

  protected String createBearerToken(long userId) {
    JwtClaimsSet claims = claimSet.claim("user", userId)
        .build();
    Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
    return "Bearer " + jwt.getTokenValue();
  }

}
