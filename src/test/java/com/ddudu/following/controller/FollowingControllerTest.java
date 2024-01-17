package com.ddudu.following.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.common.exception.BadRequestException;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.following.domain.FollowingStatus;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.request.UpdateFollowingRequest;
import com.ddudu.following.dto.response.FollowingResponse;
import com.ddudu.following.exception.FollowingErrorCode;
import com.ddudu.following.service.FollowingService;
import com.ddudu.support.ControllerTestSupport;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(FollowingController.class)
class FollowingControllerTest extends ControllerTestSupport {

  static final Faker faker = new Faker();

  @MockBean
  FollowingService followingService;

  @Nested
  class POST_팔로잉_신청_API_테스트 {

    static final String PATH = "/api/followings";

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
      ResultActions actions = mockMvc.perform(post(PATH)
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
      ResultActions actions = mockMvc.perform(post(PATH)
          .header(AUTHORIZATION, token)
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
      ResultActions actions = mockMvc.perform(post(PATH)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isCreated())
          .andExpect(header().string("location", is(PATH + "/1")));
    }

  }

  @Nested
  class PUT_팔로잉_수정_API_테스트 {

    static final String PATH = "/api/followings/{id}";

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
      ResultActions actions = mockMvc.perform(put(PATH, randomId)
          .header(AUTHORIZATION, token)
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
      ResultActions actions = mockMvc.perform(put(PATH, randomId)
          .header(AUTHORIZATION, token)
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
      ResultActions actions = mockMvc.perform(put(PATH, randomId)
          .header(AUTHORIZATION, token)
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
      ResultActions actions = mockMvc.perform(put(PATH, randomId)
          .header(AUTHORIZATION, token)
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
      ResultActions actions = mockMvc.perform(put(PATH, randomId)
          .header(AUTHORIZATION, token)
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

    static final String PATH = "/api/followings/{id}";

    long randomId;
    String token;
    MockHttpServletRequestBuilder requestBuilder;

    @BeforeEach
    void setUp() {
      randomId = faker.random()
          .nextLong(Long.MAX_VALUE);
      token = createBearerToken(randomId);
    }

    @Test
    void 팔로잉_삭제를_성공하고_204_No_Content를_반환한다() throws Exception {
      // given
      willDoNothing().given(followingService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete(PATH, randomId)
          .header(AUTHORIZATION, token));

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
      ResultActions actions = mockMvc.perform(delete(PATH, followingId)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.WRONG_OWNER.getCode())))
          .andExpect(jsonPath("$.message", is(FollowingErrorCode.WRONG_OWNER.getMessage())));
    }

  }

}
