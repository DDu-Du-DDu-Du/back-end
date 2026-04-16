package com.modoo.application.user.auth.jwt;

import com.google.common.collect.Maps;
import com.modoo.application.user.auth.config.JwtProperties;
import com.modoo.common.exception.AuthErrorCode;
import com.modoo.domain.user.auth.aggregate.RefreshToken;
import com.modoo.domain.user.auth.aggregate.vo.UserFamily;
import com.modoo.domain.user.user.aggregate.User;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenManager {

  private static final String USER_CLAIM = "user";
  private static final String AUTH_CLAIM = "auth";
  private final JwtProperties jwtProperties;
  private final JwtIssuer jwtIssuer;
  private final JwtDecoder jwtDecoder;

  public String createAccessToken(User user) {
    String authority = user.getAuthority()
        .getAuthority();

    return createAccessToken(user.getId(), authority);
  }

  public String createAccessToken(Long userId, String authority) {
    Map<String, Object> claims = Maps.newHashMap();

    claims.put(USER_CLAIM, userId);
    claims.put(AUTH_CLAIM, authority);

    return jwtIssuer.issue(claims, Duration.ofSeconds(jwtProperties.getExpiredAfter()));
  }

  public RefreshToken createRefreshToken(User user, Integer family) {
    return createRefreshToken(
        user.getId(),
        family,
        user.getAuthority().getAuthority()
    );
  }

  public RefreshToken createRefreshToken(Long userId, Integer family, String authority) {
    if (Objects.isNull(authority) || authority.isBlank()) {
      throw new IllegalArgumentException(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
    }

    UserFamily userFamily = UserFamily.builder()
        .userId(userId)
        .family(family)
        .build();
    Map<String, Object> claim = Maps.newHashMap();
    claim.put(JwtClaimNames.SUB, userFamily.getUserFamilyValue());
    claim.put(AUTH_CLAIM, authority);

    String tokenValue = jwtIssuer.issue(claim, Duration.ZERO);

    return RefreshToken.builder()
        .currentToken(tokenValue)
        .userFamily(userFamily)
        .build();
  }

  public UserFamily decodeRefreshToken(String refreshToken) {
    Jwt jwt = decode(refreshToken);
    String authority = jwt.getClaimAsString(AUTH_CLAIM);

    if (Objects.isNull(authority) || authority.isBlank()) {
      throw new UnsupportedOperationException(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
    }

    return UserFamily.builderWithString()
        .userFamilyValue(jwt.getSubject())
        .authority(authority)
        .buildWithString();
  }

  private Jwt decode(String jwt) {
    try {
      return jwtDecoder.decode(jwt);
    } catch (RuntimeException e) {
      throw new UnsupportedOperationException(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

}
