package com.ddudu.support;

import com.ddudu.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestSecretKey {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Bean
  public JwtProperties jwtProperties() {
    return new JwtProperties(secretKey);
  }

}
