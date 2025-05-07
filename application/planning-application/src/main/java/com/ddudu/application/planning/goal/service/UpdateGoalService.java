package com.ddudu.application.planning.goal.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.application.dto.goal.request.UpdateGoalRequest;
import com.ddudu.application.dto.goal.response.GoalIdResponse;
import com.ddudu.application.port.goal.in.UpdateGoalUseCase;
import com.ddudu.application.port.goal.out.GoalLoaderPort;
import com.ddudu.application.port.goal.out.UpdateGoalPort;
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
