package com.ddudu.goal.service;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.dto.response.GoalSummaryResponse;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
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
  private final UserRepository userRepository;

  @Transactional
  public CreateGoalResponse create(
      Long userId,
      @Valid
      CreateGoalRequest request
  ) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다."));

    Goal goal = Goal.builder()
        .name(request.name())
        .user(user)
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

  public GoalResponse getById(Long id) {
    Goal goal = goalRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 목표가 존재하지 않습니다."));

    return GoalResponse.from(goal);
  }

  public List<GoalSummaryResponse> getAllById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다."));

    List<Goal> goals = goalRepository.findAllByUser(user);

    return goals.stream()
        .map(GoalSummaryResponse::from)
        .toList();
  }

  @Transactional
  public void delete(Long id) {
    Goal goal = goalRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 목표가 존재하지 않습니다."));

    goal.delete();
  }

}
