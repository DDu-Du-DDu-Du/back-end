package com.ddudu.support;

import com.ddudu.old.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestProperties {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expired-after}")
  private int expiredAfter;

  @Bean
  public JwtProperties jwtProperties() {
    return new JwtProperties(secretKey, expiredAfter);
  }

}
