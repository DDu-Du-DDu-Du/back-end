package com.ddudu.auth.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.dto.request.LoginRequest;
import com.ddudu.auth.dto.response.LoginResponse;
import com.ddudu.auth.exception.AuthErrorCode;
import com.ddudu.auth.service.AuthService;
import com.ddudu.common.exception.BadCredentialsException;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.support.TestProperties;
import com.ddudu.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
@Import({WebSecurityConfig.class, TestProperties.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthControllerTest {

  static final Faker faker = new Faker();
  static final String TEST_TOKEN = "test access token";

  String email;
  String password;

  @MockBean
  AuthService authService;

  @MockBean
  UserRepository userRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  @Nested
  class POST_로그인_API_테스트 {

    static Stream<Arguments> provideLoginRequestAndString() {
      String email = faker.internet()
          .emailAddress();
      String password = faker.internet()
          .password(8, 40, true, true, true);
      String shortPassword = faker.internet()
          .password(1, 7);
      String weakPassword = "password";
      String wrongEmail = faker.internet()
          .username();

      return Stream.of(
          Arguments.of(new LoginRequest(null, password), "이메일을 찾을 수 없습니다."),
          Arguments.of(new LoginRequest(wrongEmail, password), "이메일을 찾을 수 없습니다."),
          Arguments.of(new LoginRequest(email, null), "잘못된 비밀번호 입니다."),
          Arguments.of(new LoginRequest(email, shortPassword), "잘못된 비밀번호 입니다."),
          Arguments.of(new LoginRequest(email, weakPassword), "잘못된 비밀번호 입니다.")
      );
    }

    @BeforeEach
    void setUp() {
      email = faker.internet()
          .emailAddress();
      password = faker.internet()
          .password(8, 40, true, true, true);
    }

    @ParameterizedTest
    @MethodSource("provideLoginRequestAndString")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(LoginRequest request, String message)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 없는_이메일로_로그인_시도_시_404_Not_Found를_반환한다() throws Exception {
      // given
      LoginRequest request = new LoginRequest(email, password);

      given(authService.login(any(LoginRequest.class)))
          .willThrow(new DataNotFoundException(AuthErrorCode.EMAIL_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(AuthErrorCode.EMAIL_NOT_EXISTING.getCode())));
    }

    @Test
    void 비밀번호가_일치하지_않으면_401_Unauthorized를_반환한다() throws Exception {
      // given
      LoginRequest request = new LoginRequest(email, password);

      given(authService.login(any(LoginRequest.class)))
          .willThrow(new BadCredentialsException(AuthErrorCode.BAD_CREDENTIALS));

      // when
      ResultActions actions = mockMvc.perform(post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.code", is(AuthErrorCode.BAD_CREDENTIALS.getCode())));
    }

    @Test
    void 로그인_성공_시_OK_응답을_반환한다() throws Exception {
      // given
      LoginRequest request = new LoginRequest(email, password);
      LoginResponse response = new LoginResponse(TEST_TOKEN);

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
