package com.ddudu.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.auth.jwt.JwtAuthToken;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.support.TestSecretKey;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
@Import({WebSecurityConfig.class, TestSecretKey.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserControllerTest {

  static final Faker faker = new Faker();

  @MockBean
  UserService userService;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  JwtEncoder jwtEncoder;

  String email;
  String password;
  String nickname;

  @BeforeEach
  void setUp() {
    email = faker.internet()
        .emailAddress();
    password = faker.internet()
        .password(8, 40, true, true, true);
    nickname = faker.funnyName()
        .name();
  }

  @Nested
  class POST_회원가입_API_테스트 {

    @Test
    void 회원가입을_성공하면_OK를_반환한다() throws Exception {
      // given
      SignUpRequest request = new SignUpRequest(null, email, password, nickname, null);
      SignUpResponse response = new SignUpResponse(1L, email, nickname);

      given(userService.signUp(any(SignUpRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andDo(print())
          .andExpect(status().isCreated())
          .andExpect(header().exists("location"));
    }

  }

  @Nested
  class GET_JWT_본인_확인_API_테스트 {

    static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
        .build();
    static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
        .claim("auth", Authority.NORMAL);

    @Test
    void 토큰_검증을_성공한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong();
      JwtClaimsSet claims = claimSet.claim("user", userId)
          .build();
      Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
      String token = "Bearer " + jwt.getTokenValue();
      UserResponse expected = UserResponse.builder()
          .id(userId)
          .email(email)
          .nickname(nickname)
          .build();

      given(userService.loadFromToken(any(JwtAuthToken.class))).willReturn(expected);

      // when
      ResultActions actions = mockMvc.perform(get("/api/users/me").header("Authorization", token));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(userId))
          .andDo(print());
    }

  }

}
