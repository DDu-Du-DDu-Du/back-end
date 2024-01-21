package com.ddudu.goal.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ErrorCode;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.dto.response.GoalSummaryResponse;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

  private final GoalRepository goalRepository;
  private final UserRepository userRepository;

  @Transactional
  public CreateGoalResponse create(
      Long userId, CreateGoalRequest request
  ) {
    User user = findUser(userId, GoalErrorCode.USER_NOT_EXISTING);

    Goal goal = Goal.builder()
        .name(request.name())
        .user(user)
        .color(request.color())
        .privacyType(request.privacyType())
        .build();

    return CreateGoalResponse.from(goalRepository.save(goal));
  }

  @Transactional
  public GoalResponse update(
      Long loginId, Long id, UpdateGoalRequest request
  ) {
    Goal goal = findGoal(id, GoalErrorCode.ID_NOT_EXISTING);

    checkPermission(loginId, goal);

    goal.applyGoalUpdates(
        request.name(), request.status(), request.color(), request.privacyType());

    return GoalResponse.from(goal);
  }

  public GoalResponse findById(Long loginId, Long id) {
    Goal goal = findGoal(id, GoalErrorCode.ID_NOT_EXISTING);

    checkPermission(loginId, goal);

    return GoalResponse.from(goal);
  }

  public List<GoalSummaryResponse> findAllByUser(Long loginId, Long userId) {
    if (!userId.equals(loginId)) {
      throw new ForbiddenException(GoalErrorCode.INVALID_AUTHORITY);
    }

    User user = findUser(userId, GoalErrorCode.USER_NOT_EXISTING);

    List<Goal> goals = goalRepository.findAllByUser(user);

    return goals.stream()
        .map(GoalSummaryResponse::from)
        .toList();
  }

  @Transactional
  public void delete(Long loginId, Long id) {
    goalRepository.findById(id)
        .ifPresent(goal -> {
          checkPermission(loginId, goal);
          goal.delete();
        });
  }

  private void checkPermission(Long loginId, Goal goal) {
    if (!goal.isCreatedByUser(loginId)) {
      throw new ForbiddenException(GoalErrorCode.INVALID_AUTHORITY);
    }
  }

  private User findUser(Long userId, ErrorCode errorCode) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private Goal findGoal(Long goalId, ErrorCode errorCode) {
    return goalRepository.findById(goalId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

}
