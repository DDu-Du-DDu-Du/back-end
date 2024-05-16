package com.ddudu.presentation.api.controller;

import com.ddudu.application.domain.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.DeleteGoalUseCase;
import com.ddudu.application.port.in.goal.ChangeGoalStatusUseCase;
import com.ddudu.application.port.in.goal.CreateGoalUseCase;
import com.ddudu.application.port.in.goal.RetrieveAllGoalsUseCase;
import com.ddudu.application.port.in.goal.RetrieveGoalUseCase;
import com.ddudu.application.port.in.goal.UpdateGoalUseCase;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.exception.ForbiddenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "목표 관련 API")
public class GoalController {

  private static final String GOALS_BASE_PATH = "/api/goals/";

  private final CreateGoalUseCase createGoalUseCase;
  private final RetrieveAllGoalsUseCase retrieveAllGoalsUseCase;
  private final RetrieveGoalUseCase retrieveGoalUseCase;
  private final UpdateGoalUseCase updateGoalUseCase;
  private final ChangeGoalStatusUseCase changeGoalStatusUseCase;
  private final DeleteGoalUseCase deleteGoalUseCase;

  @PostMapping
  @Operation(summary = "목표 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalIdResponse.class)
      )
  )
  public ResponseEntity<GoalIdResponse> create(
      @Login
      @Parameter(hidden = true)
      Long userId,
      @RequestBody
      @Valid
      CreateGoalRequest request
  ) {
    GoalIdResponse response = createGoalUseCase.create(userId, request);
    URI uri = URI.create(GOALS_BASE_PATH + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @PutMapping("/{id}")
  @Operation(summary = "목표 수정")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalResponse.class)
      )
  )
  @Parameter(name = "id", description = "수정할 목표의 식별자", in = ParameterIn.PATH)
  public ResponseEntity<GoalIdResponse> update(
      @Login
      @Parameter(hidden = true)
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateGoalRequest request
  ) {
    GoalIdResponse response = updateGoalUseCase.update(loginId, id, request);

    return ResponseEntity.ok()
        .body(response);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "목표 상태 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalResponse.class)
      )
  )
  @Parameter(name = "id", description = "상태를 변경할 목표의 식별자", in = ParameterIn.PATH)
  public ResponseEntity<GoalIdResponse> changeStatus(
      @Login
      @Parameter(hidden = true)
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      ChangeGoalStatusRequest request
  ) {
    GoalIdResponse response = changeGoalStatusUseCase.changeStatus(loginId, id, request);

    return ResponseEntity.ok()
        .body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "목표 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalResponse.class)
      )
  )
  @Parameter(name = "id", description = "조회할 목표의 식별자", in = ParameterIn.PATH)
  public ResponseEntity<GoalResponse> getById(
      @Login
      @Parameter(hidden = true)
      Long loginId,
      @PathVariable
      Long id
  ) {
    GoalResponse response = retrieveGoalUseCase.getById(loginId, id);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "목표 전체 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = GoalSummaryResponse.class))
      )
  )
  @Parameter(name = "userId", description = "조회할 목표의 소유자", in = ParameterIn.QUERY)
  public ResponseEntity<List<GoalSummaryResponse>> getAllByUser(
      @Login
      @Parameter(hidden = true)
      Long loginId,
      @RequestParam
      Long userId
  ) {
    checkAuthority(loginId, userId);
    List<GoalSummaryResponse> response = retrieveAllGoalsUseCase.findAllByUser(userId);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "목표 삭제")
  @ApiResponse(
      responseCode = "204"
  )
  @Parameter(name = "id", description = "삭제할 목표의 식별자", in = ParameterIn.PATH)
  public ResponseEntity<Void> delete(
      @Login
      @Parameter(hidden = true)
      Long loginId,
      @PathVariable
      Long id
  ) {
    deleteGoalUseCase.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

  private void checkAuthority(Long loginId, Long id) {
    if (!Objects.equals(loginId, id)) {
      throw new ForbiddenException(GoalErrorCode.INVALID_AUTHORITY);
    }
  }

}
