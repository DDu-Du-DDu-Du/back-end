package com.ddudu.application.user.auth.jwt;

import com.ddudu.application.user.auth.config.JwtConfig;
import com.ddudu.application.user.auth.config.TestJwtConfig;
import com.ddudu.common.dto.Authority;
import com.ddudu.fixture.UserFixture;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    value = {TestJwtConfig.class, JwtConfig.class, JwtIssuer.class},
    initializers = ConfigDataApplicationContextInitializer.class
)
@DisplayNameGeneration(ReplaceUnderscores.class)
class JwtIssuerTest {

  @Autowired
  JwtIssuer jwtIssuer;

  @Autowired
  JwtDecoder jwtDecoder;

  @Test
  void JWT_토큰을_성공적으로_발급한다() {
    // given
    Map<String, Object> claims = Maps.newHashMap();

    claims.put("user", UserFixture.getRandomId());
    claims.put("auth", Authority.NORMAL);

    // when
    String jwt = jwtIssuer.issue(claims, Duration.ofMinutes(15));

    // then
    Assertions.assertThat(jwt)
        .isNotBlank();

    ThrowingCallable decode = () -> jwtDecoder.decode(jwt);

    Assertions.assertThatNoException()
        .isThrownBy(decode);
  }

}