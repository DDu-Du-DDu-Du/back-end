package com.ddudu.application.domain.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.config.JwtConfig;
import com.ddudu.application.config.properties.JwtProperties;
import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.domain.authentication.domain.vo.UserFamily;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.fixture.UserFixture;
import com.ddudu.support.TestProperties;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    value = {TestProperties.class, JwtConfig.class, JwtIssuer.class, AuthDomainService.class},
    initializers = ConfigDataApplicationContextInitializer.class
)
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthDomainServiceTest {

  @Autowired
  JwtProperties jwtProperties;

  @Autowired
  JwtIssuer jwtIssuer;

  @Autowired
  AuthDomainService authDomainService;


  @Nested
  class 액세스_토큰_테스트 {

    @Test
    void 액세스_토큰을_발급한다() {
      // given
      User user = UserFixture.createRandomUserWithId();

      // when
      String accessToken = authDomainService.createAccessToken(user);

      // then
      assertThat(accessToken).isNotBlank();
    }

  }

  @Nested
  class 리프레시_토큰_테스트 {

    @Test
    void 리프레시_토큰을_발급한다() {
      // given
      User user = UserFixture.createRandomUserWithId();
      int family = UserFixture.getRandomPositive();

      // when
      RefreshToken refreshToken = authDomainService.createRefreshToken(user, family);

      // then
      assertThat(refreshToken.getUserId()).isEqualTo(user.getId());
      assertThat(refreshToken.getFamily()).isEqualTo(family);
    }

    @Test
    void 리프레시_토큰_문자열을_해독한다() {
      // given
      User user = UserFixture.createRandomUserWithId();
      int family = UserFixture.getRandomPositive();
      RefreshToken expected = authDomainService.createRefreshToken(user, family);

      // when
      UserFamily actual = authDomainService.decodeRefreshToken(expected.getTokenValue());

      // then
      assertThat(actual.getUserId()).isEqualTo(user.getId());
      assertThat(actual.getFamily()).isEqualTo(family);
    }

  }

}