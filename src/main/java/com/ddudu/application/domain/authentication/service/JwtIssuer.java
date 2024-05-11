package com.ddudu.application.domain.authentication.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtIssuer {

  private static final String issuer = "marco-ddudu";
  private static final String JWT = "JWT";

  private final JwtEncoder jwtEncoder;

  public String issue(Map<String, Object> claims, Duration expirationDuration) {
    Instant now = Instant.now();
    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512)
        .type(JWT)
        .build();
    JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
        .issuedAt(now)
        .expiresAt(now.plus(expirationDuration))
        .issuer(issuer);

    claims.forEach(claimSet::claim);

    JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
        jwsHeader, claimSet.build());
    Jwt jwt = jwtEncoder.encode(jwtEncoderParameters);

    return jwt.getTokenValue();
  }

}
