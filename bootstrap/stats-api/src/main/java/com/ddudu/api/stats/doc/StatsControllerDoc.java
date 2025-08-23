package com.ddudu.api.stats.doc;

import com.ddudu.application.common.dto.stats.response.AchievedStatsDetailResponse;
import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsReportResponse;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.dto.stats.response.PostponedStatsDetailResponse;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.StatsErrorExamples;
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
                          name = "9002",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = StatsErrorExamples.STATS_LOGIN_USER_NOT_FOUND
                      ),
                      @ExampleObject(
                          name = "9003",
                          description = "타겟 사용자 아이디가 유효하지 않는 경우",
                          value = StatsErrorExamples.STATS_USER_NOT_FOUND
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
                          name = "9002",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = StatsErrorExamples.STATS_LOGIN_USER_NOT_FOUND
                      ),
                      @ExampleObject(
                          name = "9003",
                          description = "타겟 사용자 아이디가 유효하지 않는 경우",
                          value = StatsErrorExamples.STATS_USER_NOT_FOUND
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

  @Operation(summary = "월별 통합 뚜두 통계 리포트")
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
                      name = "9002",
                      description = "로그인 사용자 아이디가 유효하지 않는 경우",
                      value = StatsErrorExamples.STATS_LOGIN_USER_NOT_FOUND
                  )
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "INTERNAL_SERVER_ERROR",
              content = @Content(
                  examples = @ExampleObject(
                      name = "9001",
                      description = "서버 내부 문제로 뚜두 상태 파싱에 실패한 경우",
                      value = StatsErrorExamples.STATS_INVALID_DDUDU_STATS
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
  ResponseEntity<MonthlyStatsReportResponse> collectReport(Long loginId, YearMonth yearMonth);

  @Operation(summary = "월별 목표들의 뚜두 통계 요약")
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
                      name = "9002",
                      description = "로그인 사용자 아이디가 유효하지 않는 경우",
                      value = StatsErrorExamples.STATS_LOGIN_USER_NOT_FOUND
                  )
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "INTERNAL_SERVER_ERROR",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "9001",
                          description = "서버 내부 문제로 뚜두 상태 파싱에 실패한 경우",
                          value = StatsErrorExamples.STATS_INVALID_DDUDU_STATS
                      ),
                      @ExampleObject(
                          name = "9004",
                          description = "서버 내부 문제로 뚜두 데이터가 없는 월의 통계 시도할 경우",
                          value = StatsErrorExamples.STATS_MONTHLY_STATS_EMPTY
                      ),
                      @ExampleObject(
                          name = "9005",
                          description = "서버 내부 문제로 월의 통계 시도 시 다른 월의 뚜두가 포함될 경우",
                          value = StatsErrorExamples.STATS_MONTHLY_MONTHLY_STATS_NOT_GROUPED_BY_GOAL
                      )
                  }
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
  ResponseEntity<MonthlyStatsSummaryResponse> collectSummary(Long loginId, YearMonth yearMonth);

  @Operation(summary = "월별 뚜두 달성 중심 상세 통계")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "9006",
                          description = "toMonth가 fromMonth보다 이전일 경우",
                          value = StatsErrorExamples.INVALID_TO_MONTH
                      ),
                      @ExampleObject(
                          name = "9007",
                          description = "상세통계 조회 대상 목표 아이디가 null일 경우",
                          value = StatsErrorExamples.NULL_GOAL_ID
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
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "9002",
                      description = "로그인 사용자 아이디가 유효하지 않는 경우",
                      value = StatsErrorExamples.STATS_LOGIN_USER_NOT_FOUND
                  )
              )
          )
      }
  )
  @Parameters(
      {
          @Parameter(
              name = "goalId",
              description = "상세통계 조회 대상 목표",
              in = ParameterIn.PATH,
              example = "3"
          ),
          @Parameter(
              name = "fromMonth",
              description = "상세통계 조회 대상 기간 시작월 (기본값: 이번달)",
              in = ParameterIn.QUERY,
              example = "2024-08"
          ),
          @Parameter(
              name = "toMonth",
              description = "상세통계 조회 대상 기간 마지막월 (기본값: 이번달)",
              in = ParameterIn.QUERY,
              example = "2024-08"
          )
      }
  )
  ResponseEntity<AchievedStatsDetailResponse> collectAchievedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  );

  @Operation(summary = "월별 뚜두 미루기 중심 상세 통계")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "9006",
                          description = "toMonth가 fromMonth보다 이전일 경우",
                          value = StatsErrorExamples.INVALID_TO_MONTH
                      ),
                      @ExampleObject(
                          name = "9007",
                          description = "상세통계 조회 대상 목표 아이디가 null일 경우",
                          value = StatsErrorExamples.NULL_GOAL_ID
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
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "9002",
                      description = "로그인 사용자 아이디가 유효하지 않는 경우",
                      value = StatsErrorExamples.STATS_LOGIN_USER_NOT_FOUND
                  )
              )
          )
      }
  )
  @Parameters(
      {
          @Parameter(
              name = "goalId",
              description = "상세통계 조회 대상 목표",
              in = ParameterIn.PATH,
              example = "3"
          ),
          @Parameter(
              name = "fromMonth",
              description = "상세통계 조회 대상 기간 시작월 (기본값: 이번달)",
              in = ParameterIn.QUERY,
              example = "2024-08"
          ),
          @Parameter(
              name = "toMonth",
              description = "상세통계 조회 대상 기간 마지막월 (기본값: 이번달)",
              in = ParameterIn.QUERY,
              example = "2024-08"
          )
      }
  )
  ResponseEntity<PostponedStatsDetailResponse> collectPostponedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  );

}
