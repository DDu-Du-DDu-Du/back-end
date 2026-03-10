package com.ddudu.api.user.auth.config;

import com.ddudu.api.user.auth.filter.IgnoreBearerAuthenticationFilter;
import com.ddudu.api.user.auth.jwt.AuthorityProxy;
import com.ddudu.api.user.auth.jwt.converter.JwtConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  private static final String ALL_RESOURCES = "/api/**";
  private static final String ANNOUNCEMENTS_PATH = "/api/announcements";
  private static final String ANNOUNCEMENT_DETAIL_PATH = "/api/announcements/*";
  private static final String LOGIN_PATH = "/api/auth/login/**";

  @Bean
  public SecurityFilterChain restFilterChain(
      HttpSecurity http,
      JwtConverter jwtConverter,
      IgnoreBearerAuthenticationFilter ignoreBearerAuthenticationFilter,
      AuthenticationEntryPoint bearerTokenAuthenticationEntryPointWrapper
  )
      throws Exception {
    return http
        .cors(Customizer.withDefaults())
        .securityMatchers(matcher -> matcher
            .requestMatchers(protectedApiRequestMatcher()))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .requestCache(RequestCacheConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .rememberMe(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .anonymous(anonymous -> anonymous
            .authorities(Collections.singletonList(AuthorityProxy.GUEST)))
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtConverter)
            )
            .authenticationEntryPoint(bearerTokenAuthenticationEntryPointWrapper)
        )
        .addFilterBefore(ignoreBearerAuthenticationFilter, BearerTokenAuthenticationFilter.class)
        .build();
  }

  private RequestMatcher protectedApiRequestMatcher() {
    return new AndRequestMatcher(
        new AntPathRequestMatcher(ALL_RESOURCES),
        new NegatedRequestMatcher(excludedApiRequestMatcher())
    );
  }

  private RequestMatcher excludedApiRequestMatcher() {
    return new OrRequestMatcher(
        new AntPathRequestMatcher(ANNOUNCEMENTS_PATH, HttpMethod.GET.name()),
        new AntPathRequestMatcher(ANNOUNCEMENT_DETAIL_PATH, HttpMethod.GET.name()),
        new AntPathRequestMatcher(LOGIN_PATH, HttpMethod.POST.name())
    );
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList(
        "GET",
        "POST",
        "PUT",
        "DELETE",
        "PATCH",
        "OPTIONS"
    ));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public CorsFilter corsFilter(CorsConfigurationSource corsConfigurationSource) {
    return new CorsFilter(corsConfigurationSource);
  }

}
