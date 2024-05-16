package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.goal.ChangeGoalStatusUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.UpdateGoalPort;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ChangeGoalStatusService implements ChangeGoalStatusUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final UpdateGoalPort updateGoalPort;

  @Override
  public GoalIdResponse changeStatus(Long userId, Long id, ChangeGoalStatusRequest request) {
    Goal goal = findGoal(id);

    checkAuthority(userId, goal);

    Goal updated = goal.changeStatus(GoalStatus.from(request.status()));

    return GoalIdResponse.from(updateGoalPort.update(updated));
  }

  private Goal findGoal(Long id) {
    return goalLoaderPort.findById(id)
        .orElseThrow(
            () -> new MissingResourceException(
                GoalErrorCode.ID_NOT_EXISTING.getCodeName(),
                Goal.class.getName(),
                String.valueOf(id)
            ));
  }

  private void checkAuthority(Long userId, Goal goal) {
    if (!goal.isCreatedBy(userId)) {
      throw new SecurityException(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

}
