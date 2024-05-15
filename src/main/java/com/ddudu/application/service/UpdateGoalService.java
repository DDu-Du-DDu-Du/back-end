package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.UpdateGoalUseCase;
import com.ddudu.application.port.out.GoalLoaderPort;
import com.ddudu.application.port.out.UpdateGoalPort;
import com.ddudu.application.port.out.UserLoaderPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateGoalService implements UpdateGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final UpdateGoalPort updateGoalPort;

  @Override
  public GoalResponse update(Long userId, Long id, UpdateGoalRequest request) {
    User user = findUser(userId);
    Goal goal = findGoal(id);

    checkAuthority(user, goal);

    goal.applyGoalUpdates(
        request.name(),
        GoalStatus.from(request.status()),
        request.color(),
        PrivacyType.from(request.privacyType())
    );

    return GoalResponse.from(updateGoalPort.update(goal));
  }

  private User findUser(Long userId) {
    return userLoaderPort.findById(userId)
        .orElseThrow(
            () -> new EntityNotFoundException(GoalErrorCode.USER_NOT_EXISTING.getCodeName()));
  }

  private Goal findGoal(Long id) {
    return goalLoaderPort.findById(id)
        .orElseThrow(
            () -> new EntityNotFoundException(GoalErrorCode.ID_NOT_EXISTING.getCodeName()));
  }

  private void checkAuthority(User user, Goal goal) {
    if (!goal.isCreatedBy(user.getId())) {
      throw new AccessDeniedException(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

}
