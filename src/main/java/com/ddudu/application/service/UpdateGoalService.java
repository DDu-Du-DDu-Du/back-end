package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.UpdateGoalUseCase;
import com.ddudu.application.port.out.GoalLoaderPort;
import com.ddudu.application.port.out.UpdateGoalPort;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateGoalService implements UpdateGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final UpdateGoalPort updateGoalPort;

  @Override
  public GoalIdResponse update(Long userId, Long id, UpdateGoalRequest request) {
    Goal goal = findGoal(id);

    checkAuthority(userId, goal);

    Goal updated = goal.applyGoalUpdates(
        request.name(),
        request.color(),
        PrivacyType.from(request.privacyType())
    );

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
