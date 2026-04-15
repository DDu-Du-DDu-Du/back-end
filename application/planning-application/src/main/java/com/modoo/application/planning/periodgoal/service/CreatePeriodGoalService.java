package com.modoo.application.planning.periodgoal.service;

import com.modoo.application.common.dto.periodgoal.request.CreatePeriodGoalRequest;
import com.modoo.application.common.port.periodgoal.in.CreatePeriodGoalUseCase;
import com.modoo.application.common.port.periodgoal.out.SavePeriodGoalPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.PeriodGoalErrorCode;
import com.modoo.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.modoo.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.modoo.domain.user.user.aggregate.User;
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
