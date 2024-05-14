package com.ddudu.application.domain.authentication.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.config.properties.JwtProperties;
import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.domain.user.domain.User;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class AuthDomainService {

  private final JwtProperties jwtProperties;
  private final JwtIssuer jwtIssuer;

  public String createAccessToken(User user) {
    Map<String, Object> claims = Maps.newHashMap();

    claims.put("user", user.getId());
    claims.put("auth", user.getAuthority());

    return jwtIssuer.issue(claims, Duration.ofMinutes(jwtProperties.getExpiredAfter()));
  }

  public RefreshToken createRefreshToken(User user, Integer family) {
    String userFamily = user.getId() + " " + family;
    Map<String, Object> claim = Collections.singletonMap("sub", userFamily);
    String tokenValue = jwtIssuer.issue(claim, Duration.ZERO);

    return RefreshToken.builder()
        .userFamilyValue(userFamily)
        .tokenValue(tokenValue)
        .build();
  }

}
