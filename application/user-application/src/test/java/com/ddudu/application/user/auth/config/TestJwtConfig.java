package com.ddudu.application.user.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestJwtConfig {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expired-after}")
  private int expiredAfter;

  @Bean
  public JwtProperties jwtProperties() {
    return new JwtProperties(secretKey, expiredAfter);
  }

}
