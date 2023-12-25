package com.ddudu.todo.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.config.WebSecurityConfig;
import com.ddudu.todo.dto.response.GoalInfo;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

  @Nested
  class 일별_할_일_리스트_조회_테스트 {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"2023-12-24", "2023-12-25"})
    void 주어진_날짜로_할_일_리스트_조회를_성공한다(String validDate) throws Exception {
      // given
      LocalDate date = (validDate == null || validDate.isEmpty()) ? LocalDate.now()
          : LocalDate.parse(validDate, DateTimeFormatter.ISO_DATE);
      String dateString = date.format(DateTimeFormatter.ISO_DATE);
      List<TodoListResponse> responses = createTodoListResponse();

      given(todoService.findDailyTodoList(dateString)).willReturn(responses);

      // when then
      mockMvc.perform(get("/api/todos").param("date", dateString))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].goalInfo.id").value(responses.get(0)
              .goalInfo()
              .id()))
          .andExpect(jsonPath("$[0].goalInfo.name").value(responses.get(0)
              .goalInfo()
              .name()))
          .andExpect(jsonPath("$[0].todolist[0].id").value(responses.get(0)
              .todolist()
              .get(0)
              .id()))
          .andExpect(jsonPath("$[0].todolist[0].name").value(responses.get(0)
              .todolist()
              .get(0)
              .name()))
          .andExpect(jsonPath("$[0].todolist[0].status").value(responses.get(0)
              .todolist()
              .get(0)
              .status()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "20231225"})
    void 유효하지_않은_날짜_형식으로_할_일_리스트를_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate)
        throws Exception {
      // when then
      mockMvc.perform(get("/api/todos")
              .param("date", invalidDate))
          .andExpect(status().isBadRequest())
          .andExpect(content().string("날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형식으로 입력해주세요."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-15-01", "2023-12-33"})
    void 유효하지_않은_날짜로_할_일_리스트를_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate) throws Exception {
      // when then
      mockMvc.perform(get("/api/todos")
              .param("date", invalidDate))
          .andExpect(status().isBadRequest())
          .andExpect(content().string("유효하지 않은 날짜입니다."));
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

  private List<TodoListResponse> createTodoListResponse() {
    TodoListResponse todolist = TodoListResponse.builder()
        .goalInfo(createGoalInfo())
        .todolist(Collections.singletonList(createTodoInfo()))
        .build();

    return Collections.singletonList(todolist);
  }

}