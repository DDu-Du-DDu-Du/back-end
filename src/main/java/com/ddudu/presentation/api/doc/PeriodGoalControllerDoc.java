package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.period_goal.request.CreatePeriodGoalRequest;
import com.ddudu.application.dto.period_goal.request.UpdatePeriodGoalRequest;
import com.ddudu.application.dto.period_goal.response.PeriodGoalSummary;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "이달/이주의 목표 관련 API")
public interface PeriodGoalControllerDoc {

  @Operation(summary = "기간 목표 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = IdResponse.class)
      )
  )
  ResponseEntity<IdResponse> create(Long userId, CreatePeriodGoalRequest request);

  @Operation(summary = "기간 목표 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = PeriodGoalSummary.class)
      )
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
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = IdResponse.class)
      )
  )
  ResponseEntity<IdResponse> update(Long userId, Long id, UpdatePeriodGoalRequest request);

}
