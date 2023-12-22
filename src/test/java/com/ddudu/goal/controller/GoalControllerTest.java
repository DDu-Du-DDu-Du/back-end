package com.ddudu.goal.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.config.WebSecurityConfig;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = GoalController.class)
@Import(WebSecurityConfig.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private GoalService goalService;

  @Nested
  class 목표_생성_API_테스트 {

    private String validName;
    private String validColor;

    목표_생성_API_테스트() {
      validName = "dev course";
      validColor = "F7A29D";
    }

    @Test
    void 목표_색상_보기_설정과_함께_목표를_생성할_수_있다() throws Exception {
      // given
      CreateGoalRequest request = CreateGoalRequest.builder()
          .name(validName)
          .color(validColor)
          .privacyType(PrivacyType.PUBLIC)
          .build();

      CreateGoalResponse response = new CreateGoalResponse(1L, validName, validColor);

      given(goalService.create(any(CreateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              post("/api/goals")
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
    void 목표가_입력되지_않거나_빈_문자열일_수_없다(String invalidName) throws Exception {
      // given
      CreateGoalRequest request = CreateGoalRequest.builder()
          .name(invalidName)
          .color(validColor)
          .privacyType(PrivacyType.PUBLIC)
          .build();

      given(goalService.create(any(CreateGoalRequest.class)))
          .willReturn(new CreateGoalResponse(1L, validName, validColor));

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("목표가 입력되지 않았습니다.")));
    }

    @ParameterizedTest(name = "50자를 초과하는 목표 : {0}")
    @MethodSource("provideLongString")
    void 목표는_50자를_넘을_수_없다(String longName) throws Exception {
      // given
      CreateGoalRequest request = CreateGoalRequest.builder()
          .name(longName)
          .color(validColor)
          .privacyType(PrivacyType.PUBLIC)
          .build();

      given(goalService.create(any(CreateGoalRequest.class)))
          .willReturn(new CreateGoalResponse(1L, validName, validColor));

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("목표는 최대 50자 입니다.")));
    }

    @ParameterizedTest(name = "6자를 초과하는 색상 : {0}")
    @MethodSource("provideLongString")
    void 색상은_6자를_넘을_수_없다(String longColor) throws Exception {
      // given
      CreateGoalRequest request = CreateGoalRequest.builder()
          .color(longColor)
          .name(validName)
          .privacyType(PrivacyType.PUBLIC)
          .build();

      given(goalService.create(any(CreateGoalRequest.class)))
          .willReturn(new CreateGoalResponse(1L, validName, validColor));

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("색상 코드는 6자리 16진수입니다.")));
    }

    private static List<String> provideLongString() {
      String longString = "a".repeat(100);
      return List.of(longString);
    }


  }

}
