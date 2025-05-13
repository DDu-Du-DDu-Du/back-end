package com.ddudu.application.user.auth.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.ddudu.application.user.auth.config.JwtConfig;
import com.ddudu.application.user.auth.config.TestJwtConfig;
import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import com.ddudu.domain.user.auth.aggregate.vo.UserFamily;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.UserFixture;
import org.assertj.core.api.Assertions;
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

  User user;
  int family;
  String expectedToken;

  @BeforeEach
  void setUp() {
    user = UserFixture.createRandomUserWithId();
    family = UserFixture.getRandomPositive();
    expectedToken = UserFixture.getRandomSentenceWithMax(30);
  }

  @Test
  void 액세스_토큰을_발급한다() {
    // given

    // when
    String accessToken = tokenManager.createAccessToken(user);

    // then
    Long userClaim = jwtDecoder.decode(accessToken)
        .getClaim("user");
    Assertions.assertThat(userClaim)
        .isEqualTo(user.getId());
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
  }

}