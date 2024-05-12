package com.ddudu.old.goal.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.old.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.old.goal.dto.response.GoalResponse;
import com.ddudu.old.goal.service.GoalService;
import com.ddudu.presentation.api.controller.GoalController;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
import com.ddudu.support.ControllerTestSupport;
import java.util.List;
import java.util.stream.Stream;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(GoalController.class)
class GoalControllerTest extends ControllerTestSupport {

  static final Faker faker = new Faker();

  @MockBean
  GoalService goalService;

  String name;
  Long userId;
  String token;
  String color;
  PrivacyType privacyType;

  @BeforeEach
  void setUp() {
    name = faker.lorem()
        .word();
    userId = faker.random()
        .nextLong();
    token = createBearerToken(userId);
    color = faker.color()
        .hex()
        .substring(1);
    privacyType = provideRandomPrivacy();
  }

  @Nested
  class GET_단일_목표_조회_API_테스트 {

    static final String PATH = "/api/goals/{id}";

    @Test
    void 목표_조회에_성공하면_200_OK를_반환하다() throws Exception {
      // given
      GoalResponse response = createGoalResponse();

      given(goalService.findById(anyLong(), anyLong())).willReturn(response);

      // when
      ResultActions action = mockMvc.perform(get(PATH, response.id())
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()
              .name()))
          .andExpect(jsonPath("$.color").value(response.color()))
          .andExpect(jsonPath("$.privacyType").value(response.privacyType()
              .name()));
    }

    @Test
    void ID가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long invalidId = faker.random()
          .nextLong();

      given(goalService.findById(anyLong(), anyLong()))
          .willThrow(new DataNotFoundException(GoalErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions action = mockMvc.perform(get(PATH, invalidId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code")
              .value(GoalErrorCode.ID_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message")
              .value(GoalErrorCode.ID_NOT_EXISTING.getMessage()));
    }

    @Test
    void 로그인_사용자에게_권한이_없으면_403_Forbidden을_반환한다() throws Exception {
      // given
      Long invalidUserId = faker.random()
          .nextLong();
      Long id = faker.random()
          .nextLong();

      given(goalService.findById(anyLong(), anyLong()))
          .willThrow(new ForbiddenException(GoalErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions action = mockMvc.perform(get(PATH, id)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code")
              .value(GoalErrorCode.INVALID_AUTHORITY.getCode()))
          .andExpect(jsonPath("$.message")
              .value(GoalErrorCode.INVALID_AUTHORITY.getMessage()));
    }

    private GoalResponse createGoalResponse() {
      Long id = faker.random()
          .nextLong();

      return GoalResponse.builder()
          .id(id)
          .name(name)
          .status(GoalStatus.IN_PROGRESS)
          .color(color)
          .privacyType(privacyType)
          .build();
    }

  }

  @Nested
  class GET_전체_목표_조회_API_테스트 {

    static final String PATH = "/api/goals";

    @Test
    void 사용자의_전체_목표_조회에_성공하면_200_OK를_반환한다() throws Exception {
      // given
      List<GoalSummaryResponse> response = createGoalSummaryDTO();
      GoalSummaryResponse firstElement = response.get(0);

      given(goalService.findAllByUser(anyLong(), anyLong())).willReturn(response);

      // when
      ResultActions action = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token)
          .param("userId", "1")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].id").value(firstElement.id()))
          .andExpect(jsonPath("$[0].name").value(firstElement.name()))
          .andExpect(jsonPath("$[0].status").value(firstElement.status()))
          .andExpect(jsonPath("$[0].color").value(firstElement.color()));
    }

    @Test
    void 사용자가_존재하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long invalidUserId = faker.random()
          .nextLong();

      given(goalService.findAllByUser(anyLong(), anyLong())).willThrow(
          new DataNotFoundException(GoalErrorCode.USER_NOT_EXISTING));

      // when
      ResultActions action = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token)
          .queryParam("userId", String.valueOf(invalidUserId))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code")
              .value(GoalErrorCode.USER_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message")
              .value(containsString(GoalErrorCode.USER_NOT_EXISTING.getMessage())));
    }

    @Test
    void 로그인_사용자에게_권한이_없으면_403_Forbidden을_반환한다() throws Exception {
      // given
      Long invalidLoginId = faker.random()
          .nextLong();

      given(goalService.findAllByUser(anyLong(), anyLong())).willThrow(
          new ForbiddenException(GoalErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions action = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token)
          .queryParam("userId", String.valueOf(userId))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code")
              .value(GoalErrorCode.INVALID_AUTHORITY.getCode()))
          .andExpect(jsonPath("$.message")
              .value(containsString(GoalErrorCode.INVALID_AUTHORITY.getMessage())));
    }

    private List<GoalSummaryResponse> createGoalSummaryDTO() {
      Long goalId = faker.random()
          .nextLong();
      GoalSummaryResponse goalSummaryResponse = GoalSummaryResponse.builder()
          .id(goalId)
          .name(name)
          .status(GoalStatus.IN_PROGRESS.name())
          .color(color)
          .build();

      return List.of(goalSummaryResponse);
    }

  }

  @Nested
  class PUT_목표_수정_API_테스트 {

    static final String PATH = "/api/goals/{id}";

    GoalStatus goalStatus;

    @BeforeEach
    void setUp() {
      goalStatus = provideRandomStatus();
    }

    @Test
    void 목표_수정에_성공하면_200_OK를_반환한다() throws Exception {
      // given
      Long goalId = faker.random()
          .nextLong();
      UpdateGoalRequest request = new UpdateGoalRequest(
          name, GoalStatus.IN_PROGRESS, color, privacyType);
      GoalResponse response = new GoalResponse(
          goalId, name, GoalStatus.IN_PROGRESS, color, privacyType);

      given(goalService.update(anyLong(), anyLong(), any(UpdateGoalRequest.class)))
          .willReturn(response);

      // when
      ResultActions action = mockMvc.perform(put(PATH, goalId)
          .header(AUTHORIZATION, token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()
              .name()))
          .andExpect(jsonPath("$.color").value(response.color()))
          .andExpect(jsonPath("$.privacyType").value(response.privacyType()
              .name()));
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideUpdateGoalRequestAndString")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(String cause, UpdateGoalRequest request, String message)
        throws Exception {
      // given
      Long goalId = faker.random()
          .nextLong();

      // when
      ResultActions actions = mockMvc.perform(put(PATH, goalId)
          .header(AUTHORIZATION, token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 유효하지_않은_목표_상태가_입력되면_400_Bad_Request를_반환한다() throws Exception {
      // given
      String invalidRequestJson = """
          {
              "name": "dev course",
              "status": "INVALID TYPE",
              "color": "191919",
              "privacyType": "PUBLIC"
          }
          """;

      // when
      ResultActions action = mockMvc.perform(put(PATH, 1L)
          .header(AUTHORIZATION, token)
          .content(invalidRequestJson)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code")
              .value(3))
          .andExpect(jsonPath("$.message")
              .value(containsString("GoalStatus는 [IN_PROGRESS, DONE] 중 하나여야 합니다.")));
    }

    @ParameterizedTest(name = "유효하지 않은 공개 설정: {0}")
    @ValueSource(strings = {"", " ", "INVALID_TYPE", "public"})
    void 유효하지_않은_공개_설정이_입력된_경우_Bad_Request_응답을_반환한다(String invalidPrivacyType) throws Exception {
      // given
      String invalidRequestJson = String.format("""
          {
              "name": "dev course",
              "status": "IN_PROGRESS",
              "color": "191919",
              "privacyType": "%s"
          }
          """, invalidPrivacyType);

      // when
      ResultActions action = mockMvc.perform(put(PATH, 1L)
          .header(AUTHORIZATION, token)
          .content(invalidRequestJson)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code")
              .value(3))
          .andExpect(jsonPath("$.message")
              .value(containsString("PrivacyType는 [PRIVATE, FOLLOWER, PUBLIC] 중 하나여야 합니다.")));
    }

    @Test
    void 목표가_존재하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long invalidId = faker.random()
          .nextLong();
      UpdateGoalRequest request = new UpdateGoalRequest(
          name, GoalStatus.IN_PROGRESS, color, privacyType);

      given(goalService.update(anyLong(), anyLong(), any(UpdateGoalRequest.class)))
          .willThrow(new DataNotFoundException(GoalErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions action = mockMvc.perform(put(PATH, invalidId)
          .header(AUTHORIZATION, token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code")
              .value(GoalErrorCode.ID_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message")
              .value(containsString(GoalErrorCode.ID_NOT_EXISTING.getMessage())));
    }

    @Test
    void 로그인_사용자에게_권한이_없으면_403_Forbidden을_반환한다() throws Exception {
      // given
      Long anotherUserId = faker.random()
          .nextLong();
      String invalidToken = createBearerToken(anotherUserId);
      Long goalId = faker.random()
          .nextLong();
      UpdateGoalRequest request = new UpdateGoalRequest(
          name, GoalStatus.IN_PROGRESS, color, privacyType);

      given(goalService.update(anyLong(), anyLong(), any(UpdateGoalRequest.class)))
          .willThrow(new ForbiddenException(GoalErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions action = mockMvc.perform(put(PATH, goalId)
          .header(AUTHORIZATION, invalidToken)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code")
              .value(GoalErrorCode.INVALID_AUTHORITY.getCode()))
          .andExpect(jsonPath("$.message")
              .value(containsString(GoalErrorCode.INVALID_AUTHORITY.getMessage())));
    }

    private static Stream<Arguments> provideUpdateGoalRequestAndString() {
      String validName = faker.lorem()
          .word();
      String validColor = faker.color()
          .hex()
          .substring(1);
      PrivacyType validPrivacyType = provideRandomPrivacy();
      GoalStatus validGoalStatus = provideRandomStatus();

      String blank = " ";
      String over50 = faker.lorem()
          .characters(51);
      String over6 = faker.lorem()
          .characters(7);

      return Stream.of(
          Arguments.of(
              "목표가 공백", new UpdateGoalRequest(blank, validGoalStatus, validColor, validPrivacyType),
              "목표가 입력되지 않았습니다."
          ),
          Arguments.of(
              "목표가 " + over50,
              new UpdateGoalRequest(over50, validGoalStatus, validColor, validPrivacyType),
              "목표는 최대 50자 입니다."
          ),
          Arguments.of(
              "목표 상태가 null", new UpdateGoalRequest(validName, null, validColor, validPrivacyType),
              "목표 상태가 입력되지 않았습니다."
          ),
          Arguments.of(
              "색상이 null", new UpdateGoalRequest(validName, validGoalStatus, null, validPrivacyType),
              "색상이 입력되지 않았습니다."
          ),
          Arguments.of(
              "색상이 " + over6,
              new UpdateGoalRequest(validName, validGoalStatus, over6, validPrivacyType),
              "색상 코드는 6자리 16진수입니다."
          ),
          Arguments.of(
              "공개 설정이 null ", new UpdateGoalRequest(validName, validGoalStatus, validColor, null),
              "공개 설정이 입력되지 않았습니다."
          )
      );
    }

    private static GoalStatus provideRandomStatus() {
      GoalStatus[] goalStatuses = {GoalStatus.IN_PROGRESS, GoalStatus.DONE};
      return goalStatuses[faker.random()
          .nextInt(goalStatuses.length)];
    }

  }

  @Nested
  class DELETE_목표_삭제_API_테스트 {

    static final String PATH = "/api/goals/{id}";

    @Test
    void 목표_삭제에_성공하면_204_No_Content를_반환한다() throws Exception {
      // given
      Long goalId = faker.random()
          .nextLong();

      willDoNothing().given(goalService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions action = mockMvc.perform(delete(PATH, goalId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action.andExpect(status().isNoContent());
    }

    @Test
    void 로그인_사용자에게_권한이_없으면_403_Forbidden을_반환한다() throws Exception {
      // given
      Long goalId = faker.random()
          .nextLong();

      willThrow(new ForbiddenException(GoalErrorCode.INVALID_AUTHORITY)).given(goalService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions action = mockMvc.perform(delete(PATH, goalId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      action
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code")
              .value(GoalErrorCode.INVALID_AUTHORITY.getCode()))
          .andExpect(jsonPath("$.message")
              .value(containsString(GoalErrorCode.INVALID_AUTHORITY.getMessage())));
    }

  }

  private static PrivacyType provideRandomPrivacy() {
    PrivacyType[] privacyTypes = {PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC};
    return privacyTypes[faker.random()
        .nextInt(privacyTypes.length)];
  }

}
