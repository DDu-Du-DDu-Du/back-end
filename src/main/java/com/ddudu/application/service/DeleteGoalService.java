package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.DeleteGoalUseCase;
import com.ddudu.application.port.out.DeleteGoalPort;
import com.ddudu.application.port.out.UserLoaderPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteGoalService implements DeleteGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final DeleteGoalPort deleteGoalPort;

  @Override
  public void delete(Long userId, Long id) {
    User user = findUser(userId);
    Goal goal = findGoal(id);

    checkAuthority(user, goal);

    deleteGoalPort.delete(goal);
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
