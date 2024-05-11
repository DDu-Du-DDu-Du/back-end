package com.ddudu.presentation.api.controller;

import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.CreateGoalResponse;
import com.ddudu.application.port.in.CreateGoalUseCase;
import com.ddudu.old.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.old.goal.dto.response.GoalResponse;
import com.ddudu.old.goal.dto.response.GoalSummaryResponse;
import com.ddudu.old.goal.service.GoalService;
import com.ddudu.presentation.api.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  private final GoalService goalService;

  @PostMapping
  @Operation(summary = "목표 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = CreateGoalResponse.class)
      )
  )
  public ResponseEntity<CreateGoalResponse> create(
      @Login
      @Parameter(hidden = true)
      Long userId,
      @RequestBody
      @Valid
      CreateGoalRequest request
  ) {
    CreateGoalResponse response = createGoalUseCase.create(userId, request);
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
  @Deprecated
  public ResponseEntity<GoalResponse> update(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateGoalRequest request
  ) {
    GoalResponse response = goalService.update(loginId, id, request);

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
  @Deprecated
  public ResponseEntity<GoalResponse> getById(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    GoalResponse response = goalService.findById(loginId, id);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "목표 전체 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = GoalSummaryResponse.class)
      )
  )
  @Deprecated
  public ResponseEntity<List<GoalSummaryResponse>> getAllByUser(
      @Login
      Long loginId,
      @RequestParam
      Long userId
  ) {
    List<GoalSummaryResponse> response = goalService.findAllByUser(loginId, userId);

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
  @Deprecated
  public ResponseEntity<Void> delete(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    goalService.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
