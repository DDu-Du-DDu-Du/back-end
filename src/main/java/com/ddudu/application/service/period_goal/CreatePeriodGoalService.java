package com.ddudu.application.service.period_goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.period_goal.request.CreatePeriodGoalRequest;
import com.ddudu.application.port.in.period_goal.CreatePeriodGoalUseCase;
import com.ddudu.application.port.out.period_goal.SavePeriodGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreatePeriodGoalService implements CreatePeriodGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final SavePeriodGoalPort savePeriodGoalPort;

  @Override
  public Long create(Long userId, CreatePeriodGoalRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        userId, PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());

    PeriodGoal periodGoal = PeriodGoal.builder()
        .contents(request.contents())
        .userId(user.getId())
        .type(request.type())
        .planDate(request.planDate())
        .build();

    return savePeriodGoalPort.save(periodGoal)
        .getId();
  }

}
