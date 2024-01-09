package com.ddudu.goal.controller;

import com.ddudu.common.annotation.Login;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.dto.response.GoalSummaryResponse;
import com.ddudu.goal.service.GoalService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GoalController {

  private final GoalService goalService;

  @PostMapping
  public ResponseEntity<CreateGoalResponse> create(
      @Login
      Long userId,
      @RequestBody
      @Valid
      CreateGoalRequest request
  ) {
    CreateGoalResponse response = goalService.create(userId, request);
    URI uri = URI.create("/api/goals/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @PutMapping("/{id}")
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

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
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
  public ResponseEntity<List<GoalSummaryResponse>> getAllByUser(
      @Login
      Long loginId,
      @RequestParam
      Long userId
  ) {
    List<GoalSummaryResponse> response = goalService.findAllByUser(loginId, userId);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity delete(
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
