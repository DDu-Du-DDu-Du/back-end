package com.ddudu.goal.service;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.repository.GoalRepository;
import jakarta.persistence.EntityNotFoundException;
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
        .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 목표가 존재하지 않습니다."));

    goal.applyGoalUpdates(
        request.name(), request.status(), request.color(), request.privacyType());

    return GoalResponse.from(goal);
  }

}
