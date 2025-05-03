package com.ddudu.application.domain.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.ddudu.bootstrap.common.config.JwtConfig;
import com.ddudu.bootstrap.userapi.auth.jwt.SpringOAuth2JwtIssuer;
import com.ddudu.domain.user.user.aggregate.enums.Authority;
import com.ddudu.fixture.UserFixture;
import com.ddudu.support.TestProperties;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    value = {TestProperties.class, JwtConfig.class, SpringOAuth2JwtIssuer.class},
    initializers = ConfigDataApplicationContextInitializer.class
)
@DisplayNameGeneration(ReplaceUnderscores.class)
class SpringOAuth2JwtIssuerTest {

  @Autowired
  SpringOAuth2JwtIssuer springOAuth2JwtIssuer;

  @Autowired
  JwtDecoder jwtDecoder;

  @Test
  void JWT_토큰을_성공적으로_발급한다() {
    // given
    Map<String, Object> claims = Maps.newHashMap();

    claims.put("user", UserFixture.getRandomId());
    claims.put("auth", Authority.NORMAL);

    // when
    String jwt = springOAuth2JwtIssuer.issue(claims, Duration.ofMinutes(15));

    // then
    assertThat(jwt).isNotBlank();

    ThrowingCallable decode = () -> jwtDecoder.decode(jwt);

    assertThatNoException().isThrownBy(decode);
  }

}