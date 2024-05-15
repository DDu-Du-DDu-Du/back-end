package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.RetrieveGoalUseCase;
import com.ddudu.application.port.out.GoalLoaderPort;
import com.ddudu.application.port.out.UserLoaderPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveGoalService implements RetrieveGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;

  @Override
  public GoalResponse getById(Long userId, Long id) {
    Goal goal = findGoal(id);

    checkAuthority(userId, goal);

    return GoalResponse.from(goal);
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
