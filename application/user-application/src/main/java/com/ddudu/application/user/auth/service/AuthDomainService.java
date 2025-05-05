package com.ddudu.application.user.auth.service;

import com.ddudu.application.common.config.JwtProperties;
import com.ddudu.application.user.auth.port.in.JwtIssuer;
import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import com.ddudu.domain.user.auth.aggregate.vo.UserFamily;
import com.ddudu.domain.user.user.aggregate.User;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDomainService {

  private final JwtProperties jwtProperties;
  private final JwtIssuer jwtIssuer;
  private final JwtDecoder jwtDecoder;

  public String createAccessToken(User user) {
    Map<String, Object> claims = Maps.newHashMap();

    claims.put("user", user.getId());
    claims.put("auth", user.getAuthority());

    return jwtIssuer.issue(claims, Duration.ofSeconds(jwtProperties.getExpiredAfter()));
  }

  public RefreshToken createRefreshToken(User user, Integer family) {
    UserFamily userFamily = UserFamily.builder()
        .userId(user.getId())
        .family(family)
        .build();
    Map<String, Object> claim = Collections.singletonMap(
        JwtClaimNames.SUB, userFamily.getUserFamilyValue());
    String tokenValue = jwtIssuer.issue(claim, Duration.ZERO);

    return RefreshToken.builder()
        .tokenValue(tokenValue)
        .userFamily(userFamily)
        .build();
  }

  public UserFamily decodeRefreshToken(String refreshToken) {
    Jwt jwt = jwtDecoder.decode(refreshToken);
    return UserFamily.builderWithString()
        .userFamilyValue(jwt.getClaimAsString(JwtClaimNames.SUB))
        .buildWithString();
  }

}
