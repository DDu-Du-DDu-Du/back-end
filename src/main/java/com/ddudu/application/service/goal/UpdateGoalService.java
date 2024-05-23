package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.goal.UpdateGoalUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.UpdateGoalPort;
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
    Goal goal = goalLoaderPort.getGoalOrElseThrow(id, GoalErrorCode.ID_NOT_EXISTING.getCodeName());

    goal.validateGoalCreator(userId);

    Goal updated = goal.applyGoalUpdates(
        request.name(),
        request.color(),
        PrivacyType.from(request.privacyType())
    );

    return GoalIdResponse.from(updateGoalPort.update(updated));
  }

}
