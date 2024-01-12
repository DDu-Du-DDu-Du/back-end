package com.ddudu.like.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.like.dto.request.LikeRequest;
import com.ddudu.like.dto.response.LikeResponse;
import com.ddudu.like.service.LikeService;
import com.ddudu.support.TestProperties;
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
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LikeController.class)
@Import({WebSecurityConfig.class, TestProperties.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class LikeControllerTest {

  static final Faker faker = new Faker();
  static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
      .build();
  static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
      .claim("auth", Authority.NORMAL);

  @MockBean
  LikeService likeService;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  JwtEncoder jwtEncoder;

  Long userId;
  Long todoId;
  String token;

  @BeforeEach
  void setUp() {
    userId = faker.random()
        .nextLong(Long.MAX_VALUE);
    todoId = faker.random()
        .nextLong(Long.MAX_VALUE);
    token = createBearerToken(userId);
  }

  @Nested
  class POST_좋아요_API_테스트 {

    static Stream<Arguments> provideLikeRequestAndStrings() {
      Long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Long todoId = faker.random()
          .nextLong(Long.MAX_VALUE);

      return Stream.of(
          Arguments.of(
              "사용자가 null", new LikeRequest(null, todoId),
              "좋아요를 누르려는 사용자의 아이디를 확인할 수 없습니다."
          ),
          Arguments.of(
              "할 일이 null", new LikeRequest(userId, null),
              "좋아요 대상 할 일의 아이디를 확인할 수 없습니다."
          )
      );
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideLikeRequestAndStrings")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(String cause, LikeRequest request, String message)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(post("/api/likes")
          .header("Authorization", token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 좋아요하고_201_Created를_반환한다() throws Exception {
      // given
      LikeRequest request = new LikeRequest(userId, todoId);
      LikeResponse response = createLikeResponse(userId, todoId);

      given(likeService.create(anyLong(), any(LikeRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(post("/api/likes")
          .header("Authorization", token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isCreated())
          .andExpect(header().string("location", is("/api/likes/" + response.id())));
    }

  }

  @Nested
  class DELETE_좋아요_취소_API_테스트 {

    @Test
    void 좋아요_취소를_성공하면_204_No_Content를_반환한다() throws Exception {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      willDoNothing().given(likeService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete("/api/likes/{id}", id)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNoContent());
    }

  }

  private String createBearerToken(long userId) {
    JwtClaimsSet claims = claimSet.claim("user", userId)
        .build();
    Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
    return "Bearer " + jwt.getTokenValue();
  }

  private LikeResponse createLikeResponse(Long userId, Long todoId) {
    return LikeResponse.builder()
        .id(1L)
        .userId(userId)
        .todoId(todoId)
        .build();
  }

}