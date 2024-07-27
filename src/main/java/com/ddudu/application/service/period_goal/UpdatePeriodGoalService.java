package com.ddudu.application.service.period_goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import com.ddudu.application.dto.period_goal.request.UpdatePeriodGoalRequest;
import com.ddudu.application.port.in.period_goal.UpdatePeriodGoalUseCase;
import com.ddudu.application.port.out.period_goal.PeriodGoalLoaderPort;
import com.ddudu.application.port.out.period_goal.UpdatePeriodGoalPort;
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
