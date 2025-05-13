package com.ddudu.api.stats.doc;

import com.ddudu.application.common.dto.stats.CompletionPerGoalDto;
import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.DduduErrorExamples;
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
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Stats",
    description = "스탯 관련 API"
)
public interface StatsControllerDoc {

  @Operation(summary = "주간 뚜두 완료도 조회")
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
                          value = DduduErrorExamples.DDUDU_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2006",
                          description = "타겟 사용자 아이디가 유효하지 않는 경우",
                          value = DduduErrorExamples.DDUDU_USER_NOT_EXISTING
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
              description = "조회할 뚜두의 사용자 식별자 (기본값: 로그인한 사용자)",
              in = ParameterIn.QUERY
          ),
          @Parameter(
              name = "date",
              description = "조회할 날짜 (기본값: 오늘)",
              in = ParameterIn.QUERY
          )
      }
  )
  ResponseEntity<List<DduduCompletionResponse>> getWeeklyCompletion(
      Long loginId, Long userId, LocalDate date
  );

  @Operation(summary = "월간 뚜두 완료도 조회")
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
                          value = DduduErrorExamples.DDUDU_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2006",
                          description = "타겟 사용자 아이디가 유효하지 않는 경우",
                          value = DduduErrorExamples.DDUDU_USER_NOT_EXISTING
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
              description = "조회할 뚜두의 사용자 식별자 (기본값: 로그인한 사용자)",
              in = ParameterIn.QUERY
          ),
          @Parameter(
              name = "date",
              description = "조회할 달 (기본값: 이번 달)",
              in = ParameterIn.QUERY
          )
      }
  )
  ResponseEntity<List<DduduCompletionResponse>> getMonthlyCompletion(
      Long loginId, Long userId, YearMonth yearMonth
  );

  @Operation(summary = "월별 통합 뚜두 통계")
  @ApiResponses(
      {
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
                  examples = @ExampleObject(
                      name = "2006",
                      description = "로그인 사용자 아이디가 유효하지 않은 경우",
                      value = DduduErrorExamples.DDUDU_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameter(
      name = "yearMonth",
      description = "통계 조회 대상 기간 월 (기본값: 이번 달)",
      in = ParameterIn.QUERY,
      example = "2024-08"
  )
  ResponseEntity<MonthlyStatsSummaryResponse> collectSummary(
      Long loginId, YearMonth yearMonth
  );

  @Operation(summary = "월별 목표들의 뚜두 생성 수 통계. Not Yet Implemented")
  @ApiResponses(
      {
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
                  examples = @ExampleObject(
                      name = "2006",
                      description = "로그인 사용자 아이디가 유효하지 않은 경우",
                      value = DduduErrorExamples.DDUDU_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameter(
      name = "yearMonth",
      description = "통계 조회 대상 기간 월 (기본값: 이번 달)",
      in = ParameterIn.QUERY,
      example = "2024-08"
  )
  ResponseEntity<GenericStatsResponse<CompletionPerGoalDto>> collectCreation(
      Long loginId, YearMonth yearMonth
  );

}
