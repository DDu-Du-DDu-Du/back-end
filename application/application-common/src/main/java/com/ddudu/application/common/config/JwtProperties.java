package com.ddudu.application.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("jwt")
@Getter
public class JwtProperties {

  private final String secretKey;
  private final int expiredAfter;

  @ConstructorBinding
  public JwtProperties(String secretKey, int expiredAfter) {
    this.secretKey = secretKey;
    this.expiredAfter = expiredAfter;
  }

}
