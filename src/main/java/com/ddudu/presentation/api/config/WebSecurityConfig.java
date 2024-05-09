package com.ddudu.presentation.api.config;

import com.ddudu.application.domain.authentication.service.converter.JwtConverter;
import com.ddudu.application.domain.user.domain.enums.Authority;
import com.ddudu.presentation.api.filter.SocialAuthenticationFilter;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  private static final String ALL_RESOURCES = "/api/**";

  @Bean
  public SecurityFilterChain restFilterChain(
      HttpSecurity http, JwtConverter jwtConverter,
      SocialAuthenticationFilter socialAuthenticationFilter
  )
      throws Exception {
    return http
        .securityMatchers(matcher -> matcher
            .requestMatchers(ALL_RESOURCES))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .requestCache(RequestCacheConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .rememberMe(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .anonymous(anonymous -> anonymous
            .authorities(Collections.singletonList(Authority.GUEST)))
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtConverter)))
        .addFilterBefore(socialAuthenticationFilter, BearerTokenAuthenticationFilter.class)
        .build();
  }

}
