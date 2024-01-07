package com.ddudu.goal.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.requset.UpdatePrivacyRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.dto.response.GoalSummaryResponse;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.goal.service.GoalService;
import com.ddudu.support.TestProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = GoalController.class)
@Import({WebSecurityConfig.class, TestProperties.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalControllerTest {

  static final Faker faker = new Faker();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private GoalService goalService;

  String validName;
  String validColor;

  @BeforeEach
  void setUp() {
    validName = faker.lorem()
        .word();
    validColor = faker.color()
        .hex()
        .substring(1);
  }

  @Nested
  class POST_목표_생성_API_테스트 {

    @Test
    void 목표_생성을_성공한다() throws Exception {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, PrivacyType.PUBLIC);
      CreateGoalResponse response = new CreateGoalResponse(1L, validName, validColor);

      given(goalService.create(anyLong(), any(CreateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .param("userId", "1")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isCreated())
          .andExpect(header().exists("location"))
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.color").value(response.color()));
    }

    @ParameterizedTest(name = "유효하지 않은 목표 : {0}")
    @NullAndEmptySource
    void 목표가_null_이거나_빈_문자열인_경우_Bad_Request_응답을_반환한다(String invalidName) throws Exception {
      // given
      CreateGoalRequest request = new CreateGoalRequest(
          invalidName, validColor, PrivacyType.PUBLIC);

      given(goalService.create(anyLong(), any(CreateGoalRequest.class)))
          .willReturn(CreateGoalResponse.builder()
              .build());

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .param("userId", "1")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].message")
              .value(containsString("목표가 입력되지 않았습니다.")));
    }

    @ParameterizedTest(name = "50자를 초과하는 목표 : {0}")
    @MethodSource("provide51Letters")
    void 목표가_50자를_초과하면_Bad_Request_응답을_반환한다(String longName) throws Exception {
      // given
      CreateGoalRequest request = new CreateGoalRequest(longName, validColor, PrivacyType.PUBLIC);

      given(goalService.create(anyLong(), any(CreateGoalRequest.class)))
          .willReturn(CreateGoalResponse.builder()
              .build());

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .param("userId", "1")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].message")
              .value(containsString("목표는 최대 50자 입니다.")));
    }

    @ParameterizedTest(name = "6자를 초과하는 색상 : {0}")
    @ValueSource(strings = {"7letter"})
    void 색상이_6자를_넘으면_Bad_Request_응답을_반환한다(String longColor) throws Exception {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, longColor, PrivacyType.PUBLIC);

      given(goalService.create(anyLong(), any(CreateGoalRequest.class)))
          .willReturn(CreateGoalResponse.builder()
              .build());

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .param("userId", "1")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].message")
              .value(containsString("색상 코드는 6자리 16진수입니다.")));
    }

    @Test
    void 색상이_16진수_포맷이_아닌_경우_Bad_Request_응답을_반환한다() throws Exception {
      // given
      String invalidColor = "Z123!";
      CreateGoalRequest request = new CreateGoalRequest(
          validName, invalidColor, PrivacyType.PUBLIC);

      given(goalService.create(anyLong(), any(CreateGoalRequest.class)))
          .willThrow(new InvalidParameterException(GoalErrorCode.INVALID_COLOR_FORMAT));

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .param("userId", "1")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString(GoalErrorCode.INVALID_COLOR_FORMAT.getMessage())));
    }

    private static List<String> provide51Letters() {
      String longString = "a".repeat(51);
      return List.of(longString);
    }

  }

  @Nested
  class GET_단일_목표_조회_API_테스트 {

    @Test
    void 목표_조회를_성공한다() throws Exception {
      // given
      GoalResponse response = createGoalResponse();

      given(goalService.findById(anyLong())).willReturn(response);

      // when then
      mockMvc.perform(
              get("/api/goals/{id}", response.id())
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()
              .name()))
          .andExpect(jsonPath("$.color").value(response.color()))
          .andExpect(jsonPath("$.privacyType").value(response.privacyType()
              .name()));
    }

    @Test
    void 유효하지_않은_목표_상태가_입력된_경우_Bad_Request_응답을_반환한다() throws Exception {
      // given
      String invalidRequestJson = """
          {
              "name": "dev course",
              "status": "INVALID TYPE",
              "color": "191919",
              "privacyType": "PUBLIC"
          }
          """;

      GoalResponse response = GoalResponse.builder()
          .build();

      given(goalService.update(anyLong(), any(UpdateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              put("/api/goals/{id}", 1L)
                  .content(invalidRequestJson)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("GoalStatus는 [IN_PROGRESS, DONE] 중 하나여야 합니다.")));
    }

    @Test
    void 유효하지_않은_공개_설정이_입력된_경우_Bad_Request_응답을_반환한다() throws Exception {
      // given
      String invalidRequestJson = """
          {
              "name": "dev course",
              "status": "IN_PROGRESS",
              "color": "191919",
              "privacyType": "INVALID TYPE"
          }
          """;

      GoalResponse response = GoalResponse.builder()
          .build();

      given(goalService.update(anyLong(), any(UpdateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              put("/api/goals/{id}", 1L)
                  .content(invalidRequestJson)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("PrivacyType는 [PRIVATE, FOLLOWER, PUBLIC] 중 하나여야 합니다.")));
    }

    @Test
    void ID가_유효하지_않으면_Not_Found_응답을_반환한다() throws Exception {
      // given
      Long invalidId = -1L;
      given(goalService.findById(anyLong()))
          .willThrow(new DataNotFoundException(GoalErrorCode.ID_NOT_EXISTING));

      // when then
      mockMvc.perform(
              get("/api/goals/{id}", invalidId)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message")
              .value(GoalErrorCode.ID_NOT_EXISTING.getMessage()));
    }

    private static GoalResponse createGoalResponse() {
      return GoalResponse.builder()
          .id(1L)
          .name("dev course")
          .status(GoalStatus.IN_PROGRESS)
          .color("191919")
          .privacyType(PrivacyType.PRIVATE)
          .build();
    }

  }

  @Nested
  class GET_전체_목표_조회_API_테스트 {

    @Test
    void 사용자의_전체_목표를_조회할_수_있다() throws Exception {
      // given
      List<GoalSummaryResponse> response = createGoalSummaryDTO();
      GoalSummaryResponse firstElement = response.get(0);

      given(goalService.findAllByUser(anyLong())).willReturn(response);

      // when then
      mockMvc.perform(
              get("/api/goals")
                  .param("userId", "1")
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].id").value(firstElement.id()))
          .andExpect(jsonPath("$[0].name").value(firstElement.name()))
          .andExpect(jsonPath("$[0].status").value(firstElement.status()))
          .andExpect(jsonPath("$[0].color").value(firstElement.color()));
    }

    @Test
    void 사용자가_존재하지_않은_경우_404_Not_Found_응답을_반환한다() throws Exception {
      // given
      String invalidUserId = "-1";
      given(goalService.findAllByUser(anyLong())).willThrow(
          new DataNotFoundException(GoalErrorCode.USER_NOT_EXISTING));

      // when then
      mockMvc.perform(
              get("/api/goals")
                  .queryParam("userId", invalidUserId)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message")
              .value(containsString(GoalErrorCode.USER_NOT_EXISTING.getMessage())));
    }

    private List<GoalSummaryResponse> createGoalSummaryDTO() {
      GoalSummaryResponse goalSummaryResponse = GoalSummaryResponse.builder()
          .id(1L)
          .name("dev course")
          .status(GoalStatus.IN_PROGRESS.name())
          .color("191919")
          .build();

      return List.of(goalSummaryResponse);
    }

  }

  @Nested
  class PUT_목표_수정_API_테스트 {

    @Test
    void 목표_수정을_성공한다() throws Exception {
      // given
      UpdateGoalRequest request = new UpdateGoalRequest(
          validName, GoalStatus.IN_PROGRESS, validColor, PrivacyType.PUBLIC);

      GoalResponse response = new GoalResponse(
          1L, validName, GoalStatus.IN_PROGRESS, validColor, PrivacyType.PUBLIC);

      given(goalService.update(anyLong(), any(UpdateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              put("/api/goals/{id}", 1L)
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()
              .name()))
          .andExpect(jsonPath("$.color").value(response.color()))
          .andExpect(jsonPath("$.privacyType").value(response.privacyType()
              .name()));
    }

    @Test
    void 유효하지_않은_목표_상태가_입력된_경우_Bad_Request_응답을_반환한다() throws Exception {
      // given
      String invalidRequestJson = """
          {
              "name": "dev course",
              "status": "INVALID TYPE",
              "color": "191919",
              "privacyType": "PUBLIC"
          }
          """;

      GoalResponse response = GoalResponse.builder()
          .build();

      given(goalService.update(anyLong(), any(UpdateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              put("/api/goals/{id}", 1L)
                  .content(invalidRequestJson)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("GoalStatus는 [IN_PROGRESS, DONE] 중 하나여야 합니다.")));
    }

    @Test
    void 유효하지_않은_공개_설정이_입력된_경우_Bad_Request_응답을_반환한다() throws Exception {
      // given
      String invalidRequestJson = """
          {
              "name": "dev course",
              "status": "INVALID TYPE",
              "color": "191919",
              "privacyType": "PUBLIC"
              "status": "IN_PROGRESS",
              "color": "191919",
              "privacyType": "INVALID TYPE"
          }
          """;

      GoalResponse response = GoalResponse.builder()
          .build();

      given(goalService.update(anyLong(), any(UpdateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              put("/api/goals/{id}", 1L)
                  .content(invalidRequestJson)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("GoalStatus는 [IN_PROGRESS, DONE] 중 하나여야 합니다.")));
    }

  }

  @Nested
  class DELETE_목표_삭제_API_테스트 {

    @Test
    void 목표_삭제를_성공한다() throws Exception {
      // when then
      mockMvc.perform(
              delete("/api/goals/{id}", 1L)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isNoContent());
    }

  }

  @Nested
  class PATCH_목표_공개_설정_변경_API_테스트 {

    @Test
    void 목표_공개_설정_변경을_성공한다(
    ) throws Exception {
      // given
      long goalId = faker.random()
          .nextLong();
      PrivacyType newPrivacy = PrivacyType.PUBLIC;
      UpdatePrivacyRequest request = new UpdatePrivacyRequest(newPrivacy);
      GoalResponse response = GoalResponse.builder()
          .id(goalId)
          .privacyType(newPrivacy)
          .build();

      given(goalService.updatePrivacy(anyLong(), any(UpdatePrivacyRequest.class))).willReturn(
          response);

      // when then
      mockMvc.perform(
              patch("/api/goals/{id}/privacy", goalId)
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.privacyType").value(response.privacyType()
              .name()));
    }

    @Test
    void 공개_설정에_null이_입력된_경우_Bad_Request_응답을_반환한다() throws Exception {
      // given
      long goalId = faker.random()
          .nextLong();
      UpdatePrivacyRequest request = new UpdatePrivacyRequest(null);

      // when then
      mockMvc.perform(
              patch("/api/goals/{id}/privacy", goalId)
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$[0].message", is("공개 설정이 입력되지 않았습니다.")));
    }

    @ParameterizedTest(name = "유효하지 않은 공개 설정: {0}")
    @ValueSource(strings = {"", " ", "INVALID_TYPE", "public"})
    void 유효하지_않은_공개_설정이_입력된_경우_Bad_Request_응답을_반환한다(String invalidPrivacyType) throws Exception {
      // given
      long goalId = faker.random()
          .nextLong();
      String invalidRequestJson = String.format("""
          {
              "privacyType": "%s"
          }
          """, invalidPrivacyType);

      GoalResponse response = GoalResponse.builder()
          .build();

      given(goalService.updatePrivacy(anyLong(), any(UpdatePrivacyRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              patch("/api/goals/{id}/privacy", goalId)
                  .content(invalidRequestJson)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("PrivacyType는 [PRIVATE, FOLLOWER, PUBLIC] 중 하나여야 합니다.")));
    }

  }

}