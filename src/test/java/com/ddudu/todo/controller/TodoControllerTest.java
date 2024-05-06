package com.ddudu.todo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
import com.ddudu.presentation.api.controller.TodoController;
import com.ddudu.old.todo.domain.TodoStatus;
import com.ddudu.old.todo.dto.request.CreateTodoRequest;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.GoalInfo;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoInfo;
import com.ddudu.old.todo.dto.response.TodoListResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import com.ddudu.old.todo.exception.TodoErrorCode;
import com.ddudu.old.todo.service.TodoService;
import com.ddudu.support.ControllerTestSupport;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
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

@WebMvcTest(TodoController.class)
class TodoControllerTest extends ControllerTestSupport {

  static final Faker faker = new Faker();

  @MockBean
  TodoService todoService;

  Long userId;
  String name;
  LocalDateTime beginAt;
  String token;

  @BeforeEach
  void setup() {
    userId = faker.random()
        .nextLong(Long.MAX_VALUE);
    name = faker.lorem()
        .word();
    beginAt = faker.date()
        .birthday()
        .toLocalDateTime();
    token = createBearerToken(userId);
  }

  @Nested
  class POST_할_일_생성_API_테스트 {

    static final String PATH = "/api/todos";

    private static Stream<Arguments> provideCreateTodoRequestAndString() {
      Long goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Long minusGoalId = faker.random()
          .nextLong(Long.MAX_VALUE) * -1;
      String name = faker.lorem()
          .word();
      String over50 = faker.lorem()
          .characters(51);
      LocalDateTime beginAt = LocalDateTime.now();

      return Stream.of(
          Arguments.of(
              "목표 아이디가 null", new CreateTodoRequest(null, name, beginAt), "목표 ID가 입력되지 않았습니다."
          ),
          Arguments.of(
              "목표 아이디가 음수", new CreateTodoRequest(minusGoalId, name, beginAt),
              "목표 ID는 양수입니다."
          ),
          Arguments.of(
              "할 일명이 null", new CreateTodoRequest(goalId, null, beginAt),
              "할 일이 입력되지 않았습니다."
          ),
          Arguments.of(
              "할 일명이 50자 초과", new CreateTodoRequest(goalId, over50, beginAt),
              "할 일은 최대 50자 입니다."
          )
      );
    }

    @Test
    void 할_일_생성을_성공하면_201_Created_응답을_반환한다() throws Exception {
      // given
      Long goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      CreateTodoRequest request = new CreateTodoRequest(goalId, name, beginAt);
      TodoInfo response = createTodoInfo();

      given(todoService.create(anyLong(), any(CreateTodoRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .header(AUTHORIZATION, token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isCreated())
          .andExpect(header().string("location", PATH + "/" + response.id()))
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()
              .name()));
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideCreateTodoRequestAndString")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(String cause, CreateTodoRequest request, String message)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .header(AUTHORIZATION, token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 할_일_생성_권한이_없으면_403_Forbidden를_반환한다() throws Exception {
      // given
      Long invalidLoginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String invalidToken = createBearerToken(invalidLoginId);
      Long goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      CreateTodoRequest request = new CreateTodoRequest(goalId, name, beginAt);

      given(todoService.create(anyLong(), any(CreateTodoRequest.class))).willThrow(
          new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .header(AUTHORIZATION, invalidToken)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
    }

    @Test
    void 로그인_사용자_정보가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      CreateTodoRequest request = new CreateTodoRequest(goalId, name, beginAt);

      given(todoService.create(anyLong(), any(CreateTodoRequest.class))).willThrow(
          new DataNotFoundException(TodoErrorCode.USER_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .header(AUTHORIZATION, token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(TodoErrorCode.USER_NOT_EXISTING.getCode())))
          .andExpect(jsonPath("$.message", is(TodoErrorCode.USER_NOT_EXISTING.getMessage())));
    }

    @Test
    void 목표_아이디가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long invalidGoalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      CreateTodoRequest request = new CreateTodoRequest(invalidGoalId, name, beginAt);

      given(todoService.create(anyLong(), any(CreateTodoRequest.class))).willThrow(
          new DataNotFoundException(TodoErrorCode.GOAL_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .header(AUTHORIZATION, token)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(TodoErrorCode.GOAL_NOT_EXISTING.getCode())))
          .andExpect(jsonPath("$.message", is(TodoErrorCode.GOAL_NOT_EXISTING.getMessage())));
    }

  }

  @Nested
  class GET_할_일_1개_조회_테스트 {

    static final String PATH = "/api/todos/{id}";

    @Test
    void 할_일_조회를_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      TodoResponse response = createTodoResponse();

      given(todoService.findById(anyLong(), anyLong())).willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(get(
          PATH, response.todo()
              .id())
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.goal.id").value(response.goal()
              .id()))
          .andExpect(jsonPath("$.goal.name").value(response.goal()
              .name()))
          .andExpect(jsonPath("$.todo.id").value(response.todo()
              .id()))
          .andExpect(jsonPath("$.todo.name").value(response.todo()
              .name()))
          .andExpect(jsonPath("$.todo.status").value(response.todo()
              .status()
              .name()));
    }

    @Test
    void 할_일_조회_권한이_없으면_403_Forbidden_응답을_반환한다() throws Exception {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      willThrow(new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY))
          .given(todoService)
          .findById(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(get(PATH, id)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
    }

    @Test
    void 아이디가_존재하지_않으면_404_Not_Found_응답을_반환한다() throws
        Exception {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      given(todoService.findById(anyLong(), anyLong())).willThrow(
          new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get(PATH, id)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.ID_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.ID_NOT_EXISTING.getMessage())));
    }

  }

  @Nested
  class GET_일별_할_일_리스트_조회_테스트 {

    static final String PATH = "/api/todos/daily";

    @Test
    void 주어진_날짜로_할_일_리스트_조회를_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      LocalDate date = LocalDate.now();
      List<TodoListResponse> responses = createTodoListResponse();

      given(todoService.findAllByDate(anyLong(), anyLong(), any(LocalDate.class))).willReturn(
          responses);

      // when
      ResultActions actions = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token)
          .param("date", date.toString())
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].goal.id").value(responses.get(0)
              .goal()
              .id()))
          .andExpect(jsonPath("$[0].goal.name").value(responses.get(0)
              .goal()
              .name()))
          .andExpect(jsonPath("$[0].todos[0].id").value(responses.get(0)
              .todos()
              .get(0)
              .id()))
          .andExpect(jsonPath("$[0].todos[0].name").value(responses.get(0)
              .todos()
              .get(0)
              .name()))
          .andExpect(jsonPath("$[0].todos[0].status").value(responses.get(0)
              .todos()
              .get(0)
              .status()
              .name()));
    }

    @Test
    void 날짜를_전달받지_않으면_현재_날짜로_할_일_리스트_조회를_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      List<TodoListResponse> responses = createTodoListResponse();

      given(todoService.findAllByDate(anyLong(), anyLong(), any())).willReturn(responses);

      // when
      ResultActions actions = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].goal.id").value(responses.get(0)
              .goal()
              .id()))
          .andExpect(jsonPath("$[0].goal.name").value(responses.get(0)
              .goal()
              .name()))
          .andExpect(jsonPath("$[0].todos[0].id").value(responses.get(0)
              .todos()
              .get(0)
              .id()))
          .andExpect(jsonPath("$[0].todos[0].name").value(responses.get(0)
              .todos()
              .get(0)
              .name()))
          .andExpect(jsonPath("$[0].todos[0].status").value(responses.get(0)
              .todos()
              .get(0)
              .status()
              .name()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "20231225", "2023-15-01", "2023-12-33"})
    void 유효하지_않은_날짜로_할_일_리스트를_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token)
          .param("date", invalidDate)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value(containsString("date의 형식이 유효하지 않습니다.")));
    }

    @Test
    void 로그인_사용자_ID가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long invalidLonginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String invalidToken = createBearerToken(invalidLonginId);

      given(todoService.findAllByDate(anyLong(), anyLong(), any())).willThrow(
          new DataNotFoundException(TodoErrorCode.LOGIN_USER_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, invalidToken)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getMessage())));
    }

    @Test
    void 일별_할_일_조회할_사용자_ID가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      given(todoService.findAllByDate(anyLong(), anyLong(), any())).willThrow(
          new DataNotFoundException(TodoErrorCode.USER_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get(PATH)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.USER_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.USER_NOT_EXISTING.getMessage())));
    }

  }

  @Nested
  class GET_할_일_달성률_조회_테스트 {

    static final String WEEKLY_PATH = "/api/todos/weekly";
    static final String MONTHLY_PATH = "/api/todos/monthly";

    @Test
    void 주간_할_일_달성률_조회를_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      LocalDate date = LocalDate.of(2024, 1, 1);
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(date, 7);

      given(
          todoService.findWeeklyCompletions(anyLong(), anyLong(), any(LocalDate.class))).willReturn(
          responses);

      // when
      ResultActions actions = mockMvc.perform(get(WEEKLY_PATH)
          .header(AUTHORIZATION, token)
          .param("date", date.toString())
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(7))
          .andExpect(jsonPath("$[0].date").value("2024-01-01"))
          .andExpect(jsonPath("$[0].totalCount").value(0))
          .andExpect(jsonPath("$[0].uncompletedCount").value(0));
    }

    @Test
    void 날짜를_전달받지_않으면_이번_주간의_할_일_달성률_조회를_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      LocalDate date = LocalDate.now();
      LocalDate mondayDate = date.with(DayOfWeek.MONDAY);
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(mondayDate, 7);

      given(
          todoService.findWeeklyCompletions(anyLong(), anyLong(), any(LocalDate.class))).willReturn(
          responses);

      // when
      ResultActions actions = mockMvc.perform(get(WEEKLY_PATH)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(7))
          .andExpect(jsonPath("$[0].date").value(mondayDate.toString()))
          .andExpect(jsonPath("$[0].totalCount").value(0))
          .andExpect(jsonPath("$[0].uncompletedCount").value(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "20231225", "2023-15-01", "2023-12-33"})
    void 유효하지_않은_날짜로_주간_할_일_달성률을_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(get(WEEKLY_PATH)
          .header(AUTHORIZATION, token)
          .param("date", invalidDate));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message", containsString("형식이 유효하지 않습니다")));
    }

    @Test
    void 주간_달성률_조회할_로그인_사용자_ID가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long invalidLonginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String invalidToken = createBearerToken(invalidLonginId);

      given(todoService.findWeeklyCompletions(anyLong(), anyLong(), any())).willThrow(
          new DataNotFoundException(TodoErrorCode.LOGIN_USER_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get(WEEKLY_PATH)
          .header(AUTHORIZATION, invalidToken)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getMessage())));
    }

    @Test
    void 주간_달성률_조회할_사용자_ID가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      given(todoService.findWeeklyCompletions(anyLong(), anyLong(), any())).willThrow(
          new DataNotFoundException(TodoErrorCode.USER_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get(WEEKLY_PATH)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.USER_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.USER_NOT_EXISTING.getMessage())));
    }

    @Test
    void 월간_할_일_달성률_조회를_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      YearMonth yearMonth = YearMonth.of(2024, 1);
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(
          yearMonth.atDay(1), 31);

      given(todoService.findMonthlyCompletions(anyLong(), anyLong(),
          any(YearMonth.class)
      )).willReturn(
          responses);

      // when
      ResultActions actions = mockMvc.perform(get(MONTHLY_PATH)
          .header(AUTHORIZATION, token)
          .param("date", yearMonth.toString())
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(31))
          .andExpect(jsonPath("$[0].date").value("2024-01-01"))
          .andExpect(jsonPath("$[0].totalCount").value(0))
          .andExpect(jsonPath("$[0].uncompletedCount").value(0));
    }

    @Test
    void 날짜를_전달받지_않으면_오늘_날짜를_기준으로_이번_달의_할_일_달성률_조회를_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      YearMonth yearMonth = YearMonth.now();
      int daysInMonth = yearMonth.lengthOfMonth();
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(
          yearMonth.atDay(1), daysInMonth);

      given(todoService.findMonthlyCompletions(anyLong(), anyLong(),
          any(YearMonth.class)
      )).willReturn(
          responses);

      // when
      ResultActions actions = mockMvc.perform(get(MONTHLY_PATH)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(daysInMonth))
          .andExpect(jsonPath("$[0].date").value(yearMonth.atDay(1)
              .toString()))
          .andExpect(jsonPath("$[0].totalCount").value(0))
          .andExpect(jsonPath("$[0].uncompletedCount").value(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "202312", "2023-15"})
    void 유효하지_않은_날짜로_월간_할_일_달성률을_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(get(MONTHLY_PATH)
          .header(AUTHORIZATION, token)
          .param("date", invalidDate));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message", containsString("형식이 유효하지 않습니다")));
    }

    @Test
    void 월간_달성률_조회할_로그인_사용자_ID가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      Long invalidLonginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String invalidToken = createBearerToken(invalidLonginId);

      given(
          todoService.findMonthlyCompletions(anyLong(), anyLong(), any(YearMonth.class))).willThrow(
          new DataNotFoundException(TodoErrorCode.LOGIN_USER_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get(MONTHLY_PATH)
          .header(AUTHORIZATION, invalidToken)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getMessage())));
    }

    @Test
    void 월간_달성률_조회할_사용자_ID가_유효하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      given(
          todoService.findMonthlyCompletions(anyLong(), anyLong(), any(YearMonth.class))).willThrow(
          new DataNotFoundException(TodoErrorCode.USER_NOT_EXISTING)
      );

      // when
      ResultActions actions = mockMvc.perform(get(MONTHLY_PATH)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.USER_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.USER_NOT_EXISTING.getMessage())));
    }

  }

  @Nested
  class PUT_할_일_수정_테스트 {

    static final String PATH = "/api/todos/{id}";

    Long goalId;

    static Stream<Arguments> provideUpdateTodoRequestAndStrings() {
      Long validGoalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String validName = faker.lorem()
          .word();
      LocalDateTime validBeginAt = faker.date()
          .birthday()
          .toLocalDateTime();

      Long negativeGoalId = -1 * faker.random()
          .nextLong(Long.MAX_VALUE);
      String over50 = faker.lorem()
          .characters(51);

      return Stream.of(
          Arguments.of(
              "목표 ID가 null", new UpdateTodoRequest(null, validName, validBeginAt),
              "목표 ID가 입력되지 않았습니다."
          ),
          Arguments.of(
              "목표 ID가 음수", new UpdateTodoRequest(negativeGoalId, validName, validBeginAt),
              "목표 ID는 양수입니다."
          ),
          Arguments.of(
              "할 일이 null", new UpdateTodoRequest(validGoalId, null, validBeginAt),
              "할 일이 입력되지 않았습니다."
          ),
          Arguments.of(
              "할 일이 " + over50, new UpdateTodoRequest(validGoalId, over50, validBeginAt),
              "할 일은 최대 50자 입니다."
          ),
          Arguments.of(
              "시작일이 null", new UpdateTodoRequest(validGoalId, validName, null),
              "할 일 시작일이 입력되지 않았습니다."
          )
      );
    }

    @BeforeEach
    void setup() {
      goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
    }

    @Test
    void 할_일_수정을_성공하면_200_OK_응답을_반환한다() throws Exception {
      // given
      UpdateTodoRequest request = new UpdateTodoRequest(goalId, name, beginAt);
      TodoInfo response = createTodoInfo();

      given(todoService.update(anyLong(), anyLong(), any(UpdateTodoRequest.class)))
          .willReturn(response);

      // when
      ResultActions action = mockMvc.perform(put(PATH, response.id())
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      action.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()
              .name()));
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideUpdateTodoRequestAndStrings")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(String cause, UpdateTodoRequest request, String message)
        throws Exception {
      // given
      Long todoId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ResultActions actions = mockMvc.perform(put(PATH, todoId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 로그인_사용자에게_할_일_수정_권한이_없으면_403_Forbidden을_반환한다()
        throws Exception {
      // given
      Long todoId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String invalidToken = createBearerToken(invalidUserId);
      UpdateTodoRequest request = new UpdateTodoRequest(goalId, name, beginAt);

      given(todoService.update(anyLong(), anyLong(), any(UpdateTodoRequest.class)))
          .willThrow(new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions actions = mockMvc.perform(put(PATH, todoId)
          .header(AUTHORIZATION, invalidToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
    }

    @Test
    void ID가_유효하지_않으면_404_Not_Found를_반환한다()
        throws Exception {
      // given
      Long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);
      UpdateTodoRequest request = new UpdateTodoRequest(goalId, name, beginAt);

      given(todoService.update(anyLong(), anyLong(), any(UpdateTodoRequest.class)))
          .willThrow(new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(put(PATH, invalidId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(TodoErrorCode.ID_NOT_EXISTING.getCode())))
          .andExpect(jsonPath("$.message", is(TodoErrorCode.ID_NOT_EXISTING.getMessage())));
    }

  }

  @Nested
  class PATCH_할_일_상태_변경_테스트 {

    static final String PATH = "/api/todos/{id}/status";

    @Test
    void 할_일_상태_변경을_성공하면_204_No_Content_응답을_반환한다() throws Exception {
      // given
      Long todoId = faker.random()
          .nextLong(Long.MAX_VALUE);

      willDoNothing().given(todoService)
          .updateStatus(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(patch(
          PATH, todoId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNoContent());
    }

    @Test
    void 할_일_상태_변경_권한이_없으면_403_Forbidden_응답을_반환한다() throws Exception {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      willThrow(new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY))
          .given(todoService)
          .updateStatus(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(patch(PATH, id)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
    }


    @Test
    void 아이디가_존재하지_않으면_404_Not_Found_응답을_반환한다() throws
        Exception {
      // given
      Long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);

      willThrow(new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING))
          .given(todoService)
          .updateStatus(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(patch(PATH, invalidId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNotFound());
    }


  }

  @Nested
  class DELETE_할_일_삭제_테스트 {

    static final String PATH = "/api/todos/{id}";

    @Test
    void 할_일_삭제에_성공하면_204_No_Content_응답을_반환한다() throws Exception {
      // given
      String token = createBearerToken(userId);

      // when
      ResultActions actions = mockMvc.perform(delete(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isNoContent());

    }

    @Test
    void 할_일_삭제_권한이_없으면_403_Forbidden_응답을_반환한다() throws Exception {
      // given
      String token = createBearerToken(userId);

      willThrow(new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY))
          .given(todoService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
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
        .status(TodoStatus.UNCOMPLETED)
        .build();
  }

  private TodoResponse createTodoResponse() {
    return TodoResponse.builder()
        .goal(createGoalInfo())
        .todo(createTodoInfo())
        .build();
  }

  private List<TodoListResponse> createTodoListResponse() {
    TodoListResponse todolist = TodoListResponse.builder()
        .goal(createGoalInfo())
        .todos(Collections.singletonList(createTodoInfo()))
        .build();

    return Collections.singletonList(todolist);
  }

  private List<TodoCompletionResponse> createEmptyTodoCompletionResponseList(
      LocalDate startDate, int numDays
  ) {
    return IntStream.range(0, numDays)
        .mapToObj(startDate::plusDays)
        .map(TodoCompletionResponse::createEmptyResponse)
        .toList();
  }

}
