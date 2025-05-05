package com.ddudu.application.planning.periodgoal.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.domain.planning.periodgoal.exception.PeriodGoalErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.periodgoal.dto.request.CreatePeriodGoalRequest;
import com.ddudu.application.planning.periodgoal.port.in.CreatePeriodGoalUseCase;
import com.ddudu.application.planning.periodgoal.port.out.SavePeriodGoalPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
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

    PeriodGoalType type = PeriodGoalType.from(request.type());
    PeriodGoal periodGoal = PeriodGoal.builder()
        .contents(request.contents())
        .userId(user.getId())
        .type(type)
        .planDate(request.planDate())
        .build();

    return savePeriodGoalPort.save(periodGoal)
        .getId();
  }

}
