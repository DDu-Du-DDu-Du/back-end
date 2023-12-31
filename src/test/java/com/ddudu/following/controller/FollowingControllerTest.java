package com.ddudu.following.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.response.FollowResponse;
import com.ddudu.following.exception.FollowingErrorCode;
import com.ddudu.following.service.FollowingService;
import com.ddudu.support.TestProperties;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(FollowingController.class)
@Import({WebSecurityConfig.class, TestProperties.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class FollowingControllerTest {

  static final Faker faker = new Faker();

  @MockBean
  FollowingService followingService;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  JwtEncoder jwtEncoder;

  @Nested
  class POST_팔로잉_신청_API_테스트 {

    static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
        .build();
    static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
        .claim("auth", Authority.NORMAL);
    Long followerId;
    Long followeeId;

    @BeforeEach
    void setUp() {
      followerId = faker.random()
          .nextLong();
      followeeId = faker.random()
          .nextLong();
    }

    @Test
    void 로그인한_사용자가_없으면_401_Unauthorized를_반환한다() throws Exception {
      // given
      FollowRequest request = new FollowRequest(followeeId);

      // when
      ResultActions actions = mockMvc.perform(post("/api/followings")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isUnauthorized())
          .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    @Test
    void 팔로잉_대상_사용자가_존재하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      String token = createBearerToken(followerId);
      FollowRequest request = new FollowRequest(followeeId);

      given(followingService.create(anyLong(), any(FollowRequest.class)))
          .willThrow(new DataNotFoundException(FollowingErrorCode.FOLLOWEE_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(post("/api/followings")
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.FOLLOWEE_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(FollowingErrorCode.FOLLOWEE_NOT_EXISTING.getMessage())));
    }

    @Test
    void 팔로잉_신청을_성공하고_OK를_반환한다() throws Exception {
      // given
      String token = createBearerToken(followerId);
      FollowRequest request = new FollowRequest(followeeId);
      FollowResponse response = FollowResponse.builder()
          .id(1L)
          .followerId(followerId)
          .followeeId(followeeId)
          .build();

      given(followingService.create(anyLong(), any(FollowRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(post("/api/followings")
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isCreated())
          .andExpect(header().string("location", is("/api/followings/1")));
    }

    private String createBearerToken(long userId) {
      JwtClaimsSet claims = claimSet.claim("user", userId)
          .build();
      Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
      return "Bearer " + jwt.getTokenValue();
    }

  }

}
