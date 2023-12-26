package com.ddudu.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("jwt")
@Getter
public class JwtProperties {

  private final String secretKey;

  @ConstructorBinding
  public JwtProperties(String secretKey) {
    this.secretKey = secretKey;
  }

}
