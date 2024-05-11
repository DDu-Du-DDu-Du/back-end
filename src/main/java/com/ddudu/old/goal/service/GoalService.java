package com.ddudu.old.goal.service;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.exception.ErrorCode;
import com.ddudu.old.goal.domain.OldGoalRepository;
import com.ddudu.old.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.old.goal.dto.response.GoalResponse;
import com.ddudu.old.goal.dto.response.GoalSummaryResponse;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

  private final OldGoalRepository oldGoalRepository;
  private final UserRepository userRepository;

  @Transactional
  public GoalResponse update(
      Long loginId, Long id, UpdateGoalRequest request
  ) {
    Goal goal = findGoal(id, GoalErrorCode.ID_NOT_EXISTING);

    checkPermission(loginId, goal);

    goal.applyGoalUpdates(
        request.name(), request.status(), request.color(), request.privacyType());

    oldGoalRepository.update(goal);

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

    List<Goal> goals = oldGoalRepository.findAllByUser(user);

    return goals.stream()
        .map(GoalSummaryResponse::from)
        .toList();
  }

  @Transactional
  public void delete(Long loginId, Long id) {
    oldGoalRepository.findById(id)
        .ifPresent(goal -> {
          checkPermission(loginId, goal);
          oldGoalRepository.delete(goal);
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
    return oldGoalRepository.findById(goalId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

}
