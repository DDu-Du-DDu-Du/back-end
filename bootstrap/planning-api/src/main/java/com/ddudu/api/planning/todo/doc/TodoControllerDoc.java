package com.ddudu.api.planning.todo.doc;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.dto.todo.GoalGroupedTodos;
import com.ddudu.application.common.dto.todo.SimpleTodoSearchDto;
import com.ddudu.application.common.dto.todo.request.ChangeNameRequest;
import com.ddudu.application.common.dto.todo.request.CreateTodoRequest;
import com.ddudu.application.common.dto.todo.request.MoveDateRequest;
import com.ddudu.application.common.dto.todo.request.PeriodSetupRequest;
import com.ddudu.application.common.dto.todo.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.todo.request.SetReminderRequest;
import com.ddudu.application.common.dto.todo.request.TodoSearchRequest;
import com.ddudu.application.common.dto.todo.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.todo.response.RepeatAnotherDayResponse;
import com.ddudu.application.common.dto.todo.response.TimetableResponse;
import com.ddudu.application.common.dto.todo.response.TodoDetailResponse;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.TodoErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Todo",
    description = "투두 관련 API"
)
public interface TodoControllerDoc {

  @Operation(summary = "투두 생성")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "201",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2001",
                          value = TodoErrorExamples.TODO_NULL_GOAL_VALUE
                      ),
                      @ExampleObject(
                          name = "2014",
                          value = TodoErrorExamples.TODO_NEGATIVE_OR_ZERO_GOAL_ID
                      ),
                      @ExampleObject(
                          name = "2002",
                          value = TodoErrorExamples.TODO_BLANK_NAME
                      ),
                      @ExampleObject(
                          name = "2003",
                          value = TodoErrorExamples.TODO_EXCESSIVE_NAME_LENGTH
                      ),
                      @ExampleObject(
                          name = "2015",
                          value = TodoErrorExamples.TODO_NULL_SCHEDULED_DATE
                      ),
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3009",
                      description = "해당 목표에 권한이 없는 사용자인 경우",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2008",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2005",
                          description = "목표 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_GOAL_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<IdResponse> create(Long loginId, CreateTodoRequest request);

  @Operation(summary = "투두 상세 조회")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2004",
                          description = "존재하지 않는 투두인 경우",
                          value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2008",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2006",
                          description = "타겟 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_USER_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<TodoDetailResponse> getById(Long loginId, Long id);

  @Operation(
      summary = "일간 투두 리스트 조회",
      description = "목표별로 그룹화된 일간 투두 리스트 조회 API"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2008",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2006",
                          description = "타겟 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_USER_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  @Parameters(
      {
          @Parameter(
              name = "userId",
              description = "조회할 투두의 사용자 식별자 (기본값: 로그인한 사용자)",
              in = ParameterIn.QUERY
          ),
          @Parameter(
              name = "date",
              description = "조회할 날짜 (기본값: 오늘)",
              in = ParameterIn.QUERY
          )
      }
  )
  ResponseEntity<List<GoalGroupedTodos>> getDailyList(Long loginId, Long userId, LocalDate date);

  @Operation(
      summary = "일간 타임테이블 조회",
      description = "시간별로 그룹화된 일간 투두 리스트(타임 테이블) 조회 API"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2008",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2006",
                          description = "타겟 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_USER_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  @Parameters(
      {
          @Parameter(
              name = "userId",
              description = "조회할 투두의 사용자 식별자 (기본값: 로그인한 사용자)",
              in = ParameterIn.QUERY
          ),
          @Parameter(
              name = "date",
              description = "조회할 날짜 (기본값: 오늘)",
              in = ParameterIn.QUERY
          )
      }
  )
  ResponseEntity<TimetableResponse> getDailyTimetable(Long loginId, Long userId, LocalDate date);

  @Operation(summary = "투두 이름(내용) 변경")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "투두 이름(내용) 변경 실패",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2002",
                          value = TodoErrorExamples.TODO_BLANK_NAME
                      ),
                      @ExampleObject(
                          name = "2003",
                          description = "변경할 이름이 50자가 넘는 경우",
                          value = TodoErrorExamples.TODO_EXCESSIVE_NAME_LENGTH
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2004",
                      description = "존재하지 않는 투두인 경우",
                      value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                  )
              )
          )
      }
  )
  @ApiResponse(
      responseCode = "200",
      useReturnTypeSchema = true
  )
  @Parameter(
      name = "id",
      description = "변경할 투두 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<IdResponse> changeName(Long loginId, Long id, ChangeNameRequest request);

  @Operation(summary = "투두 상태 변경")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "NO_CONTENT",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2004",
                      description = "존재하지 않는 투두인 경우",
                      value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameter(
      name = "id",
      description = "변경할 투두 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<Void> updateStatus(Long loginId, Long id);

  @Operation(summary = "투두 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "NO_CONTENT",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2004",
                      description = "존재하지 않는 투두인 경우",
                      value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameter(
      name = "id",
      description = "삭제할 투두 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<Void> delete(Long loginId, Long id);

  @Operation(summary = "투두 시작/종료시간 설정")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "NO_CONTENT",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2010",
                      value = TodoErrorExamples.TODO_UNABLE_TO_FINISH_BEFORE_BEGIN
                  )
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2004",
                      description = "존재하지 않는 투두인 경우",
                      value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          )
      }
  )
  ResponseEntity<Void> setUpPeriod(Long loginId, Long id, PeriodSetupRequest request);

  @Operation(summary = "투두 날짜 변경")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2011",
                          value = TodoErrorExamples.TODO_NULL_DATE_TO_MOVE
                      ),
                      @ExampleObject(
                          name = "2012",
                          value = TodoErrorExamples.TODO_SHOULD_POSTPONE_UNTIL_FUTURE
                      ),
                      @ExampleObject(
                          name = "2022",
                          value = TodoErrorExamples.TODO_UNABLE_TO_POSTPONE_COMPLETED_TODO
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2004",
                      description = "존재하지 않는 투두인 경우",
                      value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          )
      }
  )
  ResponseEntity<Void> moveDate(Long loginId, Long id, MoveDateRequest request);

  @Operation(summary = "투두 다른 날 반복하기")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "201",
              description = "CREATED",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2013",
                      value = TodoErrorExamples.TODO_UNABLE_TO_REPRODUCE_ON_SAME_DATE
                  )
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2004",
                      description = "존재하지 않는 투두인 경우",
                      value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          )
      }
  )
  ResponseEntity<RepeatAnotherDayResponse> repeatOnAnotherDay(
      Long loginId,
      Long id,
      RepeatAnotherDayRequest request
  );

  @Operation(summary = "투두 검색")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          )
      }
  )
  ResponseEntity<ScrollResponse<SimpleTodoSearchDto>> getList(
      Long loginId,
      @ParameterObject
      TodoSearchRequest request
  );

  @Operation(summary = "미리알림 설정")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "204",
              description = "NO CONTENT"
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2008",
                          value = TodoErrorExamples.TODO_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2004",
                          value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2017",
                          value = TodoErrorExamples.BEGIN_AT_REQUIRED_FOR_REMINDER
                      ),
                      @ExampleObject(
                          name = "2018",
                          value = TodoErrorExamples.REMINDER_NOT_AFTER_NOW
                      ),
                      @ExampleObject(
                          name = "2019",
                          value = TodoErrorExamples.ZERO_REMINDER
                      ),
                      @ExampleObject(
                          name = "2020",
                          value = TodoErrorExamples.NEGATIVE_REMINDER_INPUT_EXISTS
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          )
      }
  )
  ResponseEntity<Void> setReminder(Long loginId, Long todoId, SetReminderRequest request);

  @Operation(summary = "미리알림 취소")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "204",
              description = "NO CONTENT"
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2004",
                      description = "존재하지 않는 투두인 경우",
                      value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<Void> cancelReminder(Long loginId, Long id);

  @Operation(summary = "투두 수정")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "2007",
                      description = "해당 투두에 대한 권한이 없는 경우 (본인만 가능)",
                      value = TodoErrorExamples.TODO_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2004",
                          description = "존재하지 않는 투두인 경우",
                          value = TodoErrorExamples.TODO_ID_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2005",
                          description = "목표 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_GOAL_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2008",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = TodoErrorExamples.TODO_LOGIN_USER_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  @Parameter(
      name = "id",
      description = "수정할 투두 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<IdResponse> update(Long loginId, Long id, UpdateTodoRequest request);

}
