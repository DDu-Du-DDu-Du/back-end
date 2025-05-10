package com.ddudu.application.planning.periodgoal.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.application.common.dto.periodgoal.request.UpdatePeriodGoalRequest;
import com.ddudu.application.common.port.periodgoal.in.UpdatePeriodGoalUseCase;
import com.ddudu.application.common.port.periodgoal.out.PeriodGoalLoaderPort;
import com.ddudu.application.common.port.periodgoal.out.UpdatePeriodGoalPort;
import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.common.exception.PeriodGoalErrorCode;
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
        id, PeriodGoalErrorCode.PERIOD_GOAL_NOT_EXISTING.getCodeName());

    periodGoal.validateCreator(userId);

    PeriodGoal updatedPeriodGoal = periodGoal.update(request.contents());

    return updatePeriodGoalPort.update(updatedPeriodGoal)
        .getId();
  }

}
