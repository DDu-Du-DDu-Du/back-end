package com.ddudu.goal.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.requset.UpdatePrivacyRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.dto.response.GoalSummaryResponse;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
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
        .orElseThrow(() -> new DataNotFoundException(GoalErrorCode.USER_NOT_EXISTING));

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
        .orElseThrow(() -> new DataNotFoundException(GoalErrorCode.ID_NOT_EXISTING));

    goal.applyGoalUpdates(
        request.name(), request.status(), request.color(), request.privacyType());

    return GoalResponse.from(goal);
  }

  public GoalResponse findById(Long id) {
    Goal goal = goalRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(GoalErrorCode.ID_NOT_EXISTING));

    return GoalResponse.from(goal);
  }

  public List<GoalSummaryResponse> findAllByUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(GoalErrorCode.USER_NOT_EXISTING));

    List<Goal> goals = goalRepository.findAllByUser(user);

    return goals.stream()
        .map(GoalSummaryResponse::from)
        .toList();
  }

  @Transactional
  public void delete(Long id) {
    goalRepository.findById(id)
        .ifPresent(Goal::delete);
  }

  @Transactional
  public GoalResponse updatePrivacy(Long id, @Valid UpdatePrivacyRequest request) {
    Goal goal = goalRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(GoalErrorCode.ID_NOT_EXISTING));

    goal.applyPrivacyUpdate(request.privacyType());

    return GoalResponse.from(goal);
  }

}