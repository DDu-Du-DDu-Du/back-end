package com.modoo.application.planning.periodgoal.service;

import com.modoo.application.common.dto.periodgoal.request.UpdatePeriodGoalRequest;
import com.modoo.application.common.port.periodgoal.in.UpdatePeriodGoalUseCase;
import com.modoo.application.common.port.periodgoal.out.PeriodGoalLoaderPort;
import com.modoo.application.common.port.periodgoal.out.UpdatePeriodGoalPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.PeriodGoalErrorCode;
import com.modoo.domain.planning.periodgoal.aggregate.PeriodGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdatePeriodGoalService implements UpdatePeriodGoalUseCase {

  private final PeriodGoalLoaderPort periodGoalLoaderPort;
  private final UpdatePeriodGoalPort updatePeriodGoalPort;

  @Override
  public Long update(Long userId, Long id, UpdatePeriodGoalRequest request) {
    PeriodGoal periodGoal = periodGoalLoaderPort.getOrElseThrow(
        id,
        PeriodGoalErrorCode.PERIOD_GOAL_NOT_EXISTING.getCodeName()
    );

    periodGoal.validateCreator(userId);

    PeriodGoal updatedPeriodGoal = periodGoal.update(request.contents());

    return updatePeriodGoalPort.update(updatedPeriodGoal)
        .getId();
  }

}
