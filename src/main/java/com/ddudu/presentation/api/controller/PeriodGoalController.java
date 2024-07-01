package com.ddudu.presentation.api.controller;

import com.ddudu.application.dto.period_goal.request.CreatePeriodGoalRequest;
import com.ddudu.application.dto.period_goal.request.UpdatePeriodGoalRequest;
import com.ddudu.application.port.in.period_goal.CreatePeriodGoalUseCase;
import com.ddudu.application.port.in.period_goal.UpdatePeriodGoalUseCase;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import com.ddudu.presentation.api.doc.PeriodGoalControllerDoc;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/period-goals")
@RequiredArgsConstructor
public class PeriodGoalController implements PeriodGoalControllerDoc {

  private static final String PERIOD_GOALS_BASE_PATH = "/api/period-goals/";

  private final CreatePeriodGoalUseCase createPeriodGoalUseCase;
  private final UpdatePeriodGoalUseCase updatePeriodGoalUseCase;

  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long userId,
      @RequestBody
      @Valid
      CreatePeriodGoalRequest request
  ) {
    Long id = createPeriodGoalUseCase.create(userId, request);
    URI uri = URI.create(PERIOD_GOALS_BASE_PATH + id);

    return ResponseEntity.created(uri)
        .body(new IdResponse(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<IdResponse> update(
      @Login
      Long userId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdatePeriodGoalRequest request
  ) {
    Long updated = updatePeriodGoalUseCase.update(userId, id, request);
    
    return ResponseEntity.ok(new IdResponse(updated));
  }

}
