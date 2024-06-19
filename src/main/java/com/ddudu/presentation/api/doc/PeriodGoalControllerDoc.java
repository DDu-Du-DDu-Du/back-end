package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.period_goal.request.CreatePeriodGoalRequest;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

}
