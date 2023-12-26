package com.ddudu.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.dto.request.LoginRequest;
import com.ddudu.auth.dto.response.LoginResponse;
import com.ddudu.auth.jwt.JwtIssuer;
import com.ddudu.auth.service.AuthService;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Duration;
import java.util.Collections;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthControllerTest {

  static final Faker faker = new Faker();
  static final String SECRET_KEY = "0JOztl/SUESOG63jJ9nmeF9SJy1K82JQjDYt9sWenfUeFwbWkzScLDiWiuTOUuN9JL3RbTnpADR7UNWHVOsafw==";

  @MockBean
  AuthService authService;

  @MockBean
  UserRepository userRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  final JwtEncoder jwtEncoder = new NimbusJwtEncoder(
      new ImmutableSecret<>(SECRET_KEY.getBytes()));
  final JwtIssuer jwtIssuer = new JwtIssuer(jwtEncoder);

  @Nested
  class 로그인_API_테스트 {

    @Test
    void 로그인_성공_시_OK_응답을_반환한다() throws Exception {
      // given
      String email = faker.internet()
          .emailAddress();
      String password = faker.internet()
          .password();
      LoginRequest request = new LoginRequest(email, password);
      String jwt = jwtIssuer.issue(
          Collections.singletonMap("user", 1L), Duration.ofMinutes(15));
      LoginResponse response = new LoginResponse(jwt);

      given(authService.login(any(LoginRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk());
    }

  }

}
