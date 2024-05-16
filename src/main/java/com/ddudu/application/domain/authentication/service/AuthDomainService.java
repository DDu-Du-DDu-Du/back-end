package com.ddudu.application.domain.authentication.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.config.properties.JwtProperties;
import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.domain.authentication.domain.vo.UserFamily;
import com.ddudu.application.domain.user.domain.User;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@DomainService
@RequiredArgsConstructor
public class AuthDomainService {

  private final JwtProperties jwtProperties;
  private final JwtIssuer jwtIssuer;
  private final JwtDecoder jwtDecoder;

  public String createAccessToken(User user) {
    Map<String, Object> claims = Maps.newHashMap();

    claims.put("user", user.getId());
    claims.put("auth", user.getAuthority());

    return jwtIssuer.issue(claims, Duration.ofMinutes(jwtProperties.getExpiredAfter()));
  }

  public RefreshToken createRefreshToken(User user, Integer family) {
    UserFamily userFamily = UserFamily.builder()
        .userId(user.getId())
        .family(family)
        .build();
    Map<String, Object> claim = Collections.singletonMap("sub", userFamily.getUserFamilyValue());
    String tokenValue = jwtIssuer.issue(claim, Duration.ZERO);

    return RefreshToken.builder()
        .tokenValue(tokenValue)
        .userFamily(userFamily)
        .build();
  }

  public UserFamily decodeRefreshToken(String refreshToken) {
    Jwt jwt = jwtDecoder.decode(refreshToken);
    return UserFamily.builderWithString()
        .userFamilyValue(jwt.getClaimAsString("sub"))
        .buildWithString();
  }

}
