package com.modoo.application.planning.goal.service;

import com.modoo.application.common.dto.goal.request.UpdateGoalRequest;
import com.modoo.application.common.dto.goal.response.GoalIdResponse;
import com.modoo.application.common.port.goal.in.UpdateGoalUseCase;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.goal.out.UpdateGoalPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
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
        PrivacyType.from(request.privacyType()),
        request.priority()
    );

    return GoalIdResponse.from(updateGoalPort.update(updated));
  }

}
