package com.ddudu.goal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.config.WebSecurityConfig;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    @Test
    void 목표를_생성할_수_있다() throws Exception {
      // given
      CreateGoalRequest request = CreateGoalRequest.builder()
          .name("dev course")
          .color(null)
          .privacyType(null)
          .build();

      CreateGoalResponse response = new CreateGoalResponse(1L, "dev course", "191919");

      given(goalService.create(any(CreateGoalRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              post("/api/goals")
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("1"));
    }

  }

}
