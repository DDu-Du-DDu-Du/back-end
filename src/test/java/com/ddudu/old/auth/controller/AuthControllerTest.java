package com.ddudu.old.auth.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.infrastructure.persistence.repository.user.UserRepository;
import com.ddudu.old.auth.dto.response.MeResponse;
import com.ddudu.old.auth.service.AuthService;
import com.ddudu.presentation.api.controller.AuthController;
import com.ddudu.support.ControllerTestSupport;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTestSupport {

  static final Faker faker = new Faker();

  String email;
  String password;
  String nickname;

  @MockBean
  AuthService authService;

  @MockBean
  UserRepository userRepository;

  @Nested
  class POST_로그인_API_테스트 {

    static final String PATH = "/api/auth/login";

    @BeforeEach
    void setUp() {
      email = faker.internet()
          .emailAddress();
      password = faker.internet()
          .password(8, 40, true, true, true);
      nickname = faker.funnyName()
          .name();
    }

    @Test
    void 로그인_성공_시_OK_응답을_반환한다() {
    }

  }

  @Nested
  class GET_JWT_본인_확인_API_테스트 {

    static final String PATH = "/api/auth/me";

    @Test
    void 토큰_검증을_성공하고_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong();
      String token = createBearerToken(userId);
      MeResponse expected = MeResponse.builder()
          .id(userId)
          .email(email)
          .nickname(nickname)
          .build();

      given(authService.loadUser(anyLong())).willReturn(expected);

      // when
      ResultActions actions = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(userId));
    }

  }

}
