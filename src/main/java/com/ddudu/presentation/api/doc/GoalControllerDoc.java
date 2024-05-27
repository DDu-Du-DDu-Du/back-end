package com.ddudu.presentation.api.doc;

import com.ddudu.application.domain.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.BasicGoalWithStatusResponse;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "목표 관련 API")
public interface GoalControllerDoc {

  @Operation(summary = "목표 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalIdResponse.class)
      )
  )
  ResponseEntity<GoalIdResponse> create(Long userId, CreateGoalRequest request);

  @Operation(summary = "목표 수정")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalIdResponse.class)
      )
  )
  @Parameter(
      name = "id",
      description = "수정할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<GoalIdResponse> update(Long loginId, Long id, UpdateGoalRequest request);

  @Operation(summary = "목표 상태 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalIdResponse.class)
      )
  )
  @Parameter(
      name = "id",
      description = "상태를 변경할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<GoalIdResponse> changeStatus(
      Long loginId, Long id, ChangeGoalStatusRequest request
  );

  @Operation(summary = "목표 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalResponse.class)
      )
  )
  @Parameter(
      name = "id",
      description = "조회할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<GoalResponse> getById(Long loginId, Long id);

  @Operation(summary = "목표 전체 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = BasicGoalWithStatusResponse.class))
      )
  )
  @Parameter(
      name = "userId",
      description = "조회할 목표의 소유자",
      in = ParameterIn.QUERY
  )
  ResponseEntity<List<BasicGoalWithStatusResponse>> getAllByUser(Long loginId, Long userId);

  @Operation(summary = "목표 삭제")
  @ApiResponse(
      responseCode = "204"
  )
  @Parameter(
      name = "id",
      description = "삭제할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<Void> delete(Long loginId, Long id);

}
