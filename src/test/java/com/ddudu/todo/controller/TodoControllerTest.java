package com.ddudu.todo.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.config.WebSecurityConfig;
import com.ddudu.todo.dto.response.GoalInfo;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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

@WebMvcTest(controllers = TodoController.class)
@Import(WebSecurityConfig.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoControllerTest {

  @MockBean
  TodoService todoService;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  class 할_일_1개_조회_테스트 {

    @Test
    void 할_일_조회를_성공한다() throws Exception {
      // given
      TodoResponse response = createTodoResponse();
      given(todoService.findById(anyLong())).willReturn(response);

      // when then
      mockMvc.perform(get(
              "/api/todos/{id}", response.todoInfo()
                  .id())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.goalInfo.id").value(response.goalInfo()
              .id()))
          .andExpect(jsonPath("$.goalInfo.name").value(response.goalInfo()
              .name()))
          .andExpect(jsonPath("$.todoInfo.id").value(response.todoInfo()
              .id()))
          .andExpect(jsonPath("$.todoInfo.name").value(response.todoInfo()
              .name()))
          .andExpect(jsonPath("$.todoInfo.status").value(response.todoInfo()
              .status()));
    }

    @Test
    void 아이디가_존재하지_않으면_404_Not_Found_응답을_반환한다() throws
        Exception {
      // given
      Long invalidId = 999L;
      given(todoService.findById(anyLong())).willThrow(EntityNotFoundException.class);

      // when then
      mockMvc.perform(get("/api/todos/{id}", invalidId))
          .andExpect(status().isNotFound());
    }

  }

  private GoalInfo createGoalInfo() {
    return GoalInfo.builder()
        .id(1L)
        .name("dev course")
        .build();
  }

  private TodoInfo createTodoInfo() {
    return TodoInfo.builder()
        .id(1L)
        .name("할 일 조회 기능 구현")
        .status("UNCOMPLETED")
        .build();
  }


  private TodoResponse createTodoResponse() {
    return TodoResponse.builder()
        .goalInfo(createGoalInfo())
        .todoInfo(createTodoInfo())
        .build();
  }

}