package com.ddudu.application.user.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@ConfigurationPropertiesScan
public class JwtConfig {

  private final String secretKey;
  private final MacAlgorithm algorithm;

  public JwtConfig(JwtProperties jwtProperties) {
    this.secretKey = jwtProperties.getSecretKey();
    this.algorithm = MacAlgorithm.HS512;
  }

  @Bean
  public SecretKey secretKey() {
    return new SecretKeySpec(secretKey.getBytes(), algorithm.getName());
  }

  @Bean
  public JwtDecoder jwtDecoder(SecretKey secretKey) {
    return NimbusJwtDecoder.withSecretKey(secretKey)
        .macAlgorithm(algorithm)
        .build();
  }

  @Bean
  public JwtEncoder jwtEncoder(SecretKey secretKey) {
    ImmutableSecret<SecurityContext> immutableSecret = new ImmutableSecret<>(secretKey);

    return new NimbusJwtEncoder(immutableSecret);
  }

}
