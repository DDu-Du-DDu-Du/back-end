package com.modoo.application.user.auth.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.modoo.application.user.auth.config.JwtConfig;
import com.modoo.application.user.auth.config.TestJwtConfig;
import com.modoo.common.exception.AuthErrorCode;
import com.modoo.domain.user.auth.aggregate.RefreshToken;
import com.modoo.domain.user.auth.aggregate.vo.UserFamily;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.UserFixture;
import java.time.Duration;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    value = {TestJwtConfig.class, TokenManager.class, JwtConfig.class, JwtIssuer.class},
    initializers = ConfigDataApplicationContextInitializer.class
)
@DisplayNameGeneration(ReplaceUnderscores.class)
class TokenManagerTest {

  @Autowired
  TokenManager tokenManager;

  @Autowired
  JwtDecoder jwtDecoder;

  @Autowired
  JwtIssuer jwtIssuer;

  User user;
  int family;

  @BeforeEach
  void setUp() {
    user = UserFixture.createRandomUserWithId();
    family = UserFixture.getRandomPositive();
  }

  @Test
  void 액세스_토큰을_발급한다() {
    // given

    // when
    String accessToken = tokenManager.createAccessToken(user);

    // then
    Long userClaim = jwtDecoder.decode(accessToken)
        .getClaim("user");
    String authClaim = jwtDecoder.decode(accessToken)
        .getClaimAsString("auth");

    Assertions.assertThat(userClaim)
        .isEqualTo(user.getId());
    Assertions.assertThat(authClaim)
        .isEqualTo(user.getAuthority().getAuthority());
  }

  @Test
  void 리프레시_토큰을_발급한다() {
    // given

    // when
    RefreshToken refreshToken = tokenManager.createRefreshToken(user, family);

    // then
    assertThat(refreshToken.getUserId()).isEqualTo(user.getId());
    assertThat(refreshToken.getFamily()).isEqualTo(family);
  }

  @Test
  void 리프레시_토큰_문자열을_해독한다() {
    // given
    RefreshToken refreshToken = tokenManager.createRefreshToken(user, family);

    // when
    UserFamily actual = tokenManager.decodeRefreshToken(refreshToken.getTokenValue());

    // then
    assertThat(actual.getUserId()).isEqualTo(user.getId());
    assertThat(actual.getFamily()).isEqualTo(family);
    assertThat(actual.getAuthority()).isEqualTo(user.getAuthority().getAuthority());
  }

  @Test
  void 리프레시_토큰이_유효하지_않으면_해독을_실패한다() {
    // given
    String invalidRefreshToken = UserFixture.getRandomSentenceWithMax(20);

    // when
    ThrowingCallable decode = () ->
        tokenManager.decodeRefreshToken(invalidRefreshToken);

    // then
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(decode)
        .withMessage(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  @Test
  void 권한_claim이_없는_리프레시_토큰이면_해독을_실패한다() {
    // given
    String refreshTokenWithoutAuthority = jwtIssuer.issue(
        Collections.singletonMap("sub", user.getId() + "-" + family),
        Duration.ZERO
    );

    // when
    ThrowingCallable decode = () ->
        tokenManager.decodeRefreshToken(refreshTokenWithoutAuthority);

    // then
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(decode)
        .withMessage(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
