package com.ddudu.application.planning.periodgoal.service;

import com.ddudu.application.common.dto.periodgoal.request.CreatePeriodGoalRequest;
import com.ddudu.application.common.port.periodgoal.in.CreatePeriodGoalUseCase;
import com.ddudu.application.common.port.periodgoal.out.SavePeriodGoalPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreatePeriodGoalService implements CreatePeriodGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final SavePeriodGoalPort savePeriodGoalPort;

  @Override
  public Long create(Long userId, CreatePeriodGoalRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        userId,
        PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName()
    );
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
