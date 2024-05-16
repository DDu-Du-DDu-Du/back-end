package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.DeleteGoalUseCase;
import com.ddudu.application.port.out.goal.DeleteGoalPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteGoalService implements DeleteGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final DeleteGoalPort deleteGoalPort;

  @Override
  public void delete(Long userId, Long id) {
    Goal goal = findGoal(id);

    checkAuthority(userId, goal);

    deleteGoalPort.delete(goal);
  }

  private Goal findGoal(Long id) {
    return goalLoaderPort.findById(id)
        .orElseThrow(
            () -> new MissingResourceException(
                GoalErrorCode.ID_NOT_EXISTING.getCodeName(),
                Goal.class.getName(),
                id.toString()
            ));
  }

  private void checkAuthority(Long userId, Goal goal) {
    if (!goal.isCreatedBy(userId)) {
      throw new SecurityException(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

}
