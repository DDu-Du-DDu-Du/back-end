package com.ddudu.application.domain.authentication.service;

import com.ddudu.application.config.properties.JwtProperties;
import com.ddudu.application.domain.user.domain.User;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthDomainService {

  private final JwtProperties jwtProperties;
  private final JwtIssuer jwtIssuer;

  public String createAccessToken(User user) {
    Map<String, Object> claims = new HashMap<>();

    claims.put("user", user.getId());
    claims.put("auth", user.getAuthority());

    return jwtIssuer.issue(claims, Duration.ofMinutes(jwtProperties.getExpiredAfter()));
  }

}
