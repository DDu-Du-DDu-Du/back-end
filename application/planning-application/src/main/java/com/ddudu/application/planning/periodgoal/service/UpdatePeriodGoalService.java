package com.ddudu.application.planning.periodgoal.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.exception.PeriodGoalErrorCode;
import com.ddudu.application.planning.periodgoal.dto.request.UpdatePeriodGoalRequest;
import com.ddudu.application.planning.periodgoal.port.in.UpdatePeriodGoalUseCase;
import com.ddudu.application.planning.periodgoal.port.out.PeriodGoalLoaderPort;
import com.ddudu.application.planning.periodgoal.port.out.UpdatePeriodGoalPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdatePeriodGoalService implements UpdatePeriodGoalUseCase {

  private final PeriodGoalLoaderPort periodGoalLoaderPort;
  private final UpdatePeriodGoalPort updatePeriodGoalPort;

  @Override
  public Long update(Long userId, Long id, UpdatePeriodGoalRequest request) {
    PeriodGoal periodGoal = periodGoalLoaderPort.getOrElseThrow(
        id, PeriodGoalErrorCode.PERIOD_GOAL_NOT_EXISTING.getCodeName());

    periodGoal.validateCreator(userId);

    PeriodGoal updatedPeriodGoal = periodGoal.update(request.contents());

    return updatePeriodGoalPort.update(updatedPeriodGoal)
        .getId();
  }

}
