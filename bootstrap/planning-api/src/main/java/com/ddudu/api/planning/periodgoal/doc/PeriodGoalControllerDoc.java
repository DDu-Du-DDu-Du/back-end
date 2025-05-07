package com.ddudu.api.planning.periodgoal.doc;

import com.ddudu.application.dto.IdResponse;
import com.ddudu.application.dto.periodgoal.request.CreatePeriodGoalRequest;
import com.ddudu.application.dto.periodgoal.request.UpdatePeriodGoalRequest;
import com.ddudu.application.dto.periodgoal.response.PeriodGoalSummary;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.PeriodGoalErrorExamples;
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
import org.springframework.http.ResponseEntity;

@Tag(name = "Period Goal", description = "이달/이주의 목표 관련 API")
public interface PeriodGoalControllerDoc {

  @Operation(summary = "기간 목표 생성")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "201", description = "CREATED", useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400", description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "4001",
                          value = PeriodGoalErrorExamples.PERIOD_GOAL_CONTENTS_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "4002",
                          value = PeriodGoalErrorExamples.PERIOD_GOAL_TYPE_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "4003",
                          value = PeriodGoalErrorExamples.PERIOD_GOAL_PLAN_DATE_NOT_EXISTING
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401", description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404", description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "4004",
                      description = "로그인 사용자 아이디가 유효하지 않은 경우",
                      value = PeriodGoalErrorExamples.PERIOD_GOAL_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<IdResponse> create(Long userId, CreatePeriodGoalRequest request);

  @Operation(summary = "기간 목표 조회")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200", description = "OK", useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401", description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404", description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "4004",
                      description = "로그인 사용자 아이디가 유효하지 않은 경우",
                      value = PeriodGoalErrorExamples.PERIOD_GOAL_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameters(
      {
          @Parameter(
              name = "date",
              description = "기간 목표 날짜 (기본값: 오늘)",
              in = ParameterIn.QUERY,
              example = "2024-06-10"
          ),
          @Parameter(
              name = "type",
              description = "기간 목표 타입 (WEEK | MONTH)",
              in = ParameterIn.QUERY,
              example = "WEEK"
          )
      }
  )
  ResponseEntity<PeriodGoalSummary> getPeriodGoal(Long userId, LocalDate date, String type);

  @Operation(summary = "기간 목표 수정")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200", description = "OK", useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400", description = "BAD_REQUEST",
              content = @Content(
                  examples = @ExampleObject(
                      name = "4001",
                      value = PeriodGoalErrorExamples.PERIOD_GOAL_CONTENTS_NOT_EXISTING
                  )
              )
          ),
          @ApiResponse(
              responseCode = "401", description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403", description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "4006",
                      description = "해당 기간 목표에 대한 권한이 없는 경우",
                      value = PeriodGoalErrorExamples.PERIOD_GOAL_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404", description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "4004",
                      value = PeriodGoalErrorExamples.PERIOD_GOAL_PERIOD_GOAL_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<IdResponse> update(Long userId, Long id, UpdatePeriodGoalRequest request);

}
