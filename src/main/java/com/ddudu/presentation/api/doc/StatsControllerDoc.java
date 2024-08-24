package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.goal.response.MonthlyStatsSummaryResponse;
import com.ddudu.presentation.api.doc.error.AuthErrorExamples;
import com.ddudu.presentation.api.doc.error.DduduErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.YearMonth;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Stats",
    description = "스탯 관련 API"
)
public interface StatsControllerDoc {

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

}
