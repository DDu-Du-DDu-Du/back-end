package com.ddudu.todo.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.support.TestSecretKey;
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
@Import({WebSecurityConfig.class, TestSecretKey.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoControllerTest {

  @MockBean
  TodoService todoService;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  private TodoResponse createTodoResponse() {
    return TodoResponse.builder()
        .id(1L)
        .goalId(1L)
        .goalName("dev course")
        .name("할 일 조회 기능 구현")
        .status("UNCOMPLETED")
        .build();
  }

  @Nested
  class 할_일_1개_조회_테스트 {

    @Test
    void 할_일_조회를_성공한다() throws Exception {
      // given
      TodoResponse response = createTodoResponse();
      given(todoService.findById(anyLong())).willReturn(response);

      // when then
      mockMvc.perform(get("/api/todos/{id}", response.id())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.goalId").value(response.goalId()))
          .andExpect(jsonPath("$.goalName").value(response.goalName()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()));
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

}
