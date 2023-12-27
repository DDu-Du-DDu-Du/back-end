package com.ddudu.auth.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtIssuer {

  private static final String issuer = "marco-ddudu";
  private static final String JWT = "JWT";

  private final JwtEncoder jwtEncoder;

  public JwtIssuer(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public String issue(Map<String, Object> claims, Duration expirationDuration) {
    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512)
        .type(JWT)
        .build();
    JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plus(expirationDuration))
        .issuer(issuer);

    for (String claim : claims.keySet()) {
      claimSet.claim(claim, claims.get(claim));
    }

    JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
        jwsHeader, claimSet.build());
    Jwt jwt = jwtEncoder.encode(jwtEncoderParameters);

    return jwt.getTokenValue();
  }

}