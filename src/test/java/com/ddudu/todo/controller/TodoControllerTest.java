package com.ddudu.todo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.support.TestProperties;
import com.ddudu.todo.domain.TodoStatus;
import com.ddudu.todo.dto.request.CreateTodoRequest;
import com.ddudu.todo.dto.response.GoalInfo;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.exception.TodoErrorCode;
import com.ddudu.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TodoController.class)
@Import({WebSecurityConfig.class, TestProperties.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoControllerTest {

  static final Faker faker = new Faker();
  static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
      .build();
  static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
      .claim("auth", Authority.NORMAL);

  @MockBean
  TodoService todoService;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  JwtEncoder jwtEncoder;

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

    @Test
    void 할_일_생성을_성공한다() throws Exception {
      // given
      Long goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      ;
      CreateTodoRequest request = new CreateTodoRequest(goalId, name, beginAt);
      TodoInfo response = createTodoInfo();

      given(todoService.create(anyLong(), any(CreateTodoRequest.class)))
          .willReturn(response);

      // when then
      mockMvc.perform(
              post("/api/todos")
                  .header("Authorization", token)
                  .content(objectMapper.writeValueAsString(request))
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isCreated())
          .andExpect(header().exists("location"))
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.name").value(response.name()))
          .andExpect(jsonPath("$.status").value(response.status()
              .name()));
    }

  }

  @Nested
  class GET_할_일_1개_조회_테스트 {

    @Test
    void 할_일_조회를_성공한다() throws Exception {
      // given
      TodoResponse response = createTodoResponse();
      given(todoService.findById(anyLong(), anyLong())).willReturn(response);

      // when then
      mockMvc.perform(get(
              "/api/todos/{id}", response.todoInfo()
                  .id())
              .header("Authorization", token)
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
              .status()
              .name()));
    }

    @Test
    void 아이디가_존재하지_않으면_404_Not_Found_응답을_반환한다() throws
        Exception {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);
      given(todoService.findById(anyLong(), anyLong())).willThrow(DataNotFoundException.class);

      // when then
      mockMvc.perform(get("/api/todos/{id}", id)
              .header("Authorization", token))
          .andExpect(status().isNotFound());
    }

    @Test
    void 할_일_조회_권한이_없으면_403_Forbidden_응답을_반환한다() throws Exception {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      willThrow(new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY))
          .given(todoService)
          .findById(anyLong(), anyLong());

      // when then
      mockMvc.perform(get("/api/todos/{id}", id)
              .header("Authorization", token))
          .andExpect(status().isForbidden())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
    }

  }

  @Nested
  class GET_일별_할_일_리스트_조회_테스트 {

    @Test
    void 주어진_날짜로_할_일_리스트_조회를_성공한다() throws Exception {
      // given
      LocalDate date = LocalDate.now();
      List<TodoListResponse> responses = createTodoListResponse();

      given(todoService.findAllByDate(anyLong(), any(LocalDate.class))).willReturn(responses);

      // when then
      mockMvc.perform(get("/api/todos")
              .header("Authorization", token)
              .param("date", date.toString()))
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
              .status()
              .name()));
    }

    @Test
    void 날짜를_전달받지_않으면_현재_날짜로_할_일_리스트_조회를_성공한다() throws Exception {
      // given
      List<TodoListResponse> responses = createTodoListResponse();
      given(todoService.findAllByDate(anyLong(), any())).willReturn(responses);

      // when then
      mockMvc.perform(get("/api/todos")
              .header("Authorization", token))
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
              .status()
              .name()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "20231225", "2023-15-01", "2023-12-33"})
    void 유효하지_않은_날짜로_할_일_리스트를_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate)
        throws Exception {
      // when then
      mockMvc.perform(get("/api/todos")
              .header("Authorization", token)
              .param("date", invalidDate))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value(containsString("date의 형식이 유효하지 않습니다.")));
    }

  }

  @Nested
  class GET_할_일_달성률_조회_테스트 {

    @Test
    void 주간_할_일_달성률_조회를_성공한다() throws Exception {
      // given
      LocalDate date = LocalDate.of(2024, 1, 1);
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(date, 7);
      given(todoService.findWeeklyCompletions(anyLong(), any(LocalDate.class))).willReturn(
          responses);

      // when then
      mockMvc.perform(get("/api/todos/weekly")
              .header("Authorization", token)
              .param("date", date.toString())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(7))
          .andExpect(jsonPath("$[0].date").value("2024-01-01"))
          .andExpect(jsonPath("$[0].totalTodos").value(0))
          .andExpect(jsonPath("$[0].uncompletedTodos").value(0));
    }

    @Test
    void 날짜를_전달받지_않으면_이번_주간의_할_일_달성률_조회를_성공한다() throws Exception {
      // given
      LocalDate date = LocalDate.now();
      LocalDate mondayDate = date.with(DayOfWeek.MONDAY);
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(mondayDate, 7);
      given(todoService.findWeeklyCompletions(anyLong(), any(LocalDate.class))).willReturn(
          responses);

      // when then
      mockMvc.perform(get("/api/todos/weekly")
              .header("Authorization", token)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(7))
          .andExpect(jsonPath("$[0].date").value(mondayDate.toString()))
          .andExpect(jsonPath("$[0].totalTodos").value(0))
          .andExpect(jsonPath("$[0].uncompletedTodos").value(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "20231225", "2023-15-01", "2023-12-33"})
    void 유효하지_않은_날짜로_주간_할_일_달성률을_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate)
        throws Exception {
      // when then
      mockMvc.perform(get("/api/todos/weekly")
              .header("Authorization", token)
              .param("date", invalidDate))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message", containsString("형식이 유효하지 않습니다")));
    }

    @Test
    void 월간_할_일_달성률_조회를_성공한다() throws Exception {
      // given
      YearMonth yearMonth = YearMonth.of(2024, 1);
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(
          yearMonth.atDay(1), 31);
      given(todoService.findMonthlyCompletions(anyLong(), any(YearMonth.class))).willReturn(
          responses);

      // when then
      mockMvc.perform(get("/api/todos/monthly")
              .header("Authorization", token)
              .param("date", yearMonth.toString())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(31))
          .andExpect(jsonPath("$[0].date").value("2024-01-01"))
          .andExpect(jsonPath("$[0].totalTodos").value(0))
          .andExpect(jsonPath("$[0].uncompletedTodos").value(0));
    }

    @Test
    void 날짜를_전달받지_않으면_오늘_날짜를_기준으로_이번_달의_할_일_달성률_조회를_성공한다() throws Exception {
      // given
      YearMonth yearMonth = YearMonth.now();
      int daysInMonth = yearMonth.lengthOfMonth();
      List<TodoCompletionResponse> responses = createEmptyTodoCompletionResponseList(
          yearMonth.atDay(1), daysInMonth);
      given(todoService.findMonthlyCompletions(anyLong(), any(YearMonth.class))).willReturn(
          responses);

      // when then
      mockMvc.perform(get("/api/todos/monthly")
              .header("Authorization", token)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(daysInMonth))
          .andExpect(jsonPath("$[0].date").value(yearMonth.atDay(1)
              .toString()))
          .andExpect(jsonPath("$[0].totalTodos").value(0))
          .andExpect(jsonPath("$[0].uncompletedTodos").value(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "202312", "2023-15"})
    void 유효하지_않은_날짜로_월간_할_일_달성률을_조회하면_400_Bad_Request_응답을_반환한다(String invalidDate)
        throws Exception {
      // when then
      mockMvc.perform(get("/api/todos/monthly")
              .header("Authorization", token)
              .param("date", invalidDate))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message", containsString("형식이 유효하지 않습니다")));
    }

  }

  @Nested
  class PATCH_할_일_상태_변경_테스트 {

    @Test
    void 할_일_상태_변경을_성공한다() throws Exception {
      // given
      TodoResponse response = createTodoResponse();
      given(todoService.updateStatus(anyLong(), anyLong())).willReturn(response);

      // when then
      mockMvc.perform(patch(
              "/api/todos/{id}/status", response.todoInfo()
                  .id())
              .header("Authorization", token)
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
              .status()
              .name()));
    }

    @Test
    void 아이디가_존재하지_않으면_404_Not_Found_응답을_반환한다() throws
        Exception {
      // given
      Long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);
      given(todoService.updateStatus(anyLong(), anyLong())).willThrow(DataNotFoundException.class);

      // when then
      mockMvc.perform(patch("/api/todos/{id}/status", invalidId)
              .header("Authorization", token)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    void 할_일_상태_변경_권한이_없으면_403_Forbidden_응답을_반환한다() throws Exception {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      willThrow(new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY))
          .given(todoService)
          .updateStatus(anyLong(), anyLong());

      // when then
      mockMvc.perform(patch("/api/todos/{id}/status", id)
              .header("Authorization", token)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
    }

  }

  @Nested
  class DELETE_할_일_삭제_테스트 {

    @Test
    void 할_일_삭제에_성공한다() throws Exception {
      // given
      String token = createBearerToken(userId);

      // when then
      mockMvc.perform(
              delete("/api/todos/{id}", userId)
                  .header("Authorization", token)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isNoContent());

    }

    @Test
    void 할_일_삭제_권한이_없으면_403_Forbidden_응답을_반환한다() throws Exception {
      // given
      String token = createBearerToken(userId);

      willThrow(new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY))
          .given(todoService)
          .delete(anyLong(), anyLong());

      // when then
      mockMvc.perform(
              delete("/api/todos/{id}", userId)
                  .header("Authorization", token)
                  .contentType(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isForbidden())
          .andExpect(
              jsonPath("$.code", is(TodoErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(
              jsonPath("$.message", is(TodoErrorCode.INVALID_AUTHORITY.getMessage())));
    }

  }

  private String createBearerToken(long userId) {
    JwtClaimsSet claims = claimSet.claim("user", userId)
        .build();
    Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
    return "Bearer " + jwt.getTokenValue();
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

  private List<TodoCompletionResponse> createEmptyTodoCompletionResponseList(
      LocalDate startDate, int numDays
  ) {
    return IntStream.range(0, numDays)
        .mapToObj(startDate::plusDays)
        .map(TodoCompletionResponse::createEmptyResponse)
        .toList();
  }

}