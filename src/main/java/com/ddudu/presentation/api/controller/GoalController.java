package com.ddudu.presentation.api.controller;

import com.ddudu.application.domain.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.BasicGoalWithStatusResponse;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.goal.ChangeGoalStatusUseCase;
import com.ddudu.application.port.in.goal.CreateGoalUseCase;
import com.ddudu.application.port.in.goal.DeleteGoalUseCase;
import com.ddudu.application.port.in.goal.RetrieveAllGoalsUseCase;
import com.ddudu.application.port.in.goal.RetrieveGoalUseCase;
import com.ddudu.application.port.in.goal.UpdateGoalUseCase;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.doc.GoalControllerDoc;
import com.ddudu.presentation.api.exception.ForbiddenException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
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
public class GoalController implements GoalControllerDoc {

  private static final String GOALS_BASE_PATH = "/api/goals/";

  private final CreateGoalUseCase createGoalUseCase;
  private final RetrieveAllGoalsUseCase retrieveAllGoalsUseCase;
  private final RetrieveGoalUseCase retrieveGoalUseCase;
  private final UpdateGoalUseCase updateGoalUseCase;
  private final ChangeGoalStatusUseCase changeGoalStatusUseCase;
  private final DeleteGoalUseCase deleteGoalUseCase;

  @PostMapping
  public ResponseEntity<GoalIdResponse> create(
      @Login
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
  public ResponseEntity<GoalIdResponse> update(
      @Login
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
  public ResponseEntity<GoalIdResponse> changeStatus(
      @Login
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
  public ResponseEntity<GoalResponse> getById(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    GoalResponse response = retrieveGoalUseCase.getById(loginId, id);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<BasicGoalWithStatusResponse>> getAllByUser(
      @Login
      Long loginId,
      @RequestParam
      Long userId
  ) {
    checkAuthority(loginId, userId);
    List<BasicGoalWithStatusResponse> response = retrieveAllGoalsUseCase.findAllByUser(userId);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
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
