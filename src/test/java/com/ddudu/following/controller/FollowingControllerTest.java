package com.ddudu.following.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.exception.BadRequestException;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.following.domain.FollowingStatus;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.request.UpdateFollowingRequest;
import com.ddudu.following.dto.response.FollowingResponse;
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
  static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
      .build();
  static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
      .claim("auth", Authority.NORMAL);

  @MockBean
  FollowingService followingService;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  JwtEncoder jwtEncoder;

  private String createBearerToken(long userId) {
    JwtClaimsSet claims = claimSet.claim("user", userId)
        .build();
    Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
    return "Bearer " + jwt.getTokenValue();
  }

  @Nested
  class POST_팔로잉_신청_API_테스트 {

    Long followerId;
    Long followeeId;

    @BeforeEach
    void setUp() {
      followerId = faker.random()
          .nextLong(Long.MAX_VALUE);
      followeeId = faker.random()
          .nextLong(Long.MAX_VALUE);
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
      FollowingResponse response = FollowingResponse.builder()
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

  }

  @Nested
  class PUT_팔로잉_수정_API_테스트 {

    long randomId;
    String token;

    @BeforeEach
    void setUp() {
      randomId = faker.random()
          .nextLong();
      token = createBearerToken(randomId);
    }

    @Test
    void 요청시_팔로잉_상태가_누락되면_400_Bad_Request를_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(null);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willReturn(new FollowingResponse(randomId, null, null, null));

      // when
      ResultActions actions = mockMvc.perform(put("/api/followings/{id}", randomId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is("요청할 팔로잉 상태는 필수값입니다.")));
    }

    @Test
    void 존재하지_않는_팔로잉_아이디면_404_Not_Found를_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willThrow(new DataNotFoundException(FollowingErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(put("/api/followings/{id}", randomId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.ID_NOT_EXISTING.getCode())))
          .andExpect(jsonPath("$.message", is(FollowingErrorCode.ID_NOT_EXISTING.getMessage())));
    }

    @Test
    void 요청_상태로_수정_시도_시_400_Bad_Request를_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.REQUESTED);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willThrow(new BadRequestException(FollowingErrorCode.REQUEST_UNAVAILABLE));

      // when
      ResultActions actions = mockMvc.perform(put("/api/followings/{id}", randomId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.REQUEST_UNAVAILABLE.getCode())))
          .andExpect(
              jsonPath("$.message", is(FollowingErrorCode.REQUEST_UNAVAILABLE.getMessage())));
    }

    @Test
    void 로그인한_사용자와_팔로잉의_주인이_다르면_403_Forbidden을_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willThrow(new ForbiddenException(FollowingErrorCode.WRONG_OWNER));

      // when
      ResultActions actions = mockMvc.perform(put("/api/followings/{id}", randomId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.WRONG_OWNER.getCode())))
          .andExpect(jsonPath("$.message", is(FollowingErrorCode.WRONG_OWNER.getMessage())));
    }

    @Test
    void 팔로잉_상태_변경을_성공하고_OK를_반환한다() throws Exception {
      // given
      long followingId = faker.random()
          .nextLong();
      long followeeId = faker.random()
          .nextLong();
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);
      FollowingResponse response = new FollowingResponse(
          followingId, randomId, followeeId, FollowingStatus.FOLLOWING);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(put("/api/followings/{id}", randomId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id", is(followingId)))
          .andExpect(jsonPath("$.status", is(FollowingStatus.FOLLOWING.name())));
    }

  }

  @Nested
  class DELETE_팔로잉_삭제_API_테스트 {

    long randomId;
    String token;

    @BeforeEach
    void setUp() {
      randomId = faker.random()
          .nextLong();
      token = createBearerToken(randomId);
    }

    @Test
    void 팔로잉_삭제를_성공하고_No_Content를_반환한다() throws Exception {
      // given
      long followingId = faker.random()
          .nextLong(Long.MAX_VALUE);

      willDoNothing().given(followingService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete("/api/followings/{id}", followingId)
          .header("Authorization", token));

      // then
      actions.andExpect(status().isNoContent());
    }

    @Test
    void 로그인한_사용자와_팔로워가_다를_경우_403_Forbidden을_반환한다() throws Exception {
      // given
      long followingId = faker.random()
          .nextLong(Long.MAX_VALUE);

      willThrow(new ForbiddenException(FollowingErrorCode.WRONG_OWNER))
          .given(followingService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete("/api/followings/{id}", followingId)
          .header("Authorization", token));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.WRONG_OWNER.getCode())))
          .andExpect(jsonPath("$.message", is(FollowingErrorCode.WRONG_OWNER.getMessage())));
    }

  }

}
