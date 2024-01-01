package com.ddudu.goal.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.goal.repository.GoalRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class GoalService {

  private final GoalRepository goalRepository;

  @Transactional
  public CreateGoalResponse create(@Valid CreateGoalRequest request) {
    Goal goal = Goal.builder()
        .name(request.name())
        .color(request.color())
        .privacyType(request.privacyType())
        .build();

    return CreateGoalResponse.from(goalRepository.save(goal));
  }

  @Transactional
  public GoalResponse update(Long id, @Valid UpdateGoalRequest request) {
    Goal goal = goalRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(GoalErrorCode.ID_NOT_EXISTING));

    goal.applyGoalUpdates(
        request.name(), request.status(), request.color(), request.privacyType());

    return GoalResponse.from(goal);
  }

}
