package com.ddudu.api.planning.periodgoal.controller;

import static java.util.Objects.isNull;

import com.ddudu.application.planning.periodgoal.dto.request.CreatePeriodGoalRequest;
import com.ddudu.application.planning.periodgoal.dto.request.UpdatePeriodGoalRequest;
import com.ddudu.application.planning.periodgoal.dto.response.PeriodGoalSummary;
import com.ddudu.application.planning.periodgoal.port.in.CreatePeriodGoalUseCase;
import com.ddudu.application.planning.periodgoal.port.in.RetrievePeriodGoalUseCase;
import com.ddudu.application.planning.periodgoal.port.in.UpdatePeriodGoalUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import com.ddudu.bootstrap.common.dto.response.IdResponse;
import com.ddudu.api.planning.periodgoal.doc.PeriodGoalControllerDoc;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/period-goals")
@RequiredArgsConstructor
public class PeriodGoalController implements PeriodGoalControllerDoc {

  private static final String PERIOD_GOALS_BASE_PATH = "/api/period-goals/";

  private final CreatePeriodGoalUseCase createPeriodGoalUseCase;
  private final RetrievePeriodGoalUseCase retrievePeriodGoalUseCase;
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

  @GetMapping
  public ResponseEntity<PeriodGoalSummary> getPeriodGoal(
      @Login
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date,
      String type
  ) {
    date = isNull(date) ? LocalDate.now() : date;
    PeriodGoalSummary response = retrievePeriodGoalUseCase.retrieve(userId, date, type);

    return ResponseEntity.ok(response);
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
