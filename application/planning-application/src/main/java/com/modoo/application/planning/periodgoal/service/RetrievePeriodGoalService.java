package com.modoo.application.planning.periodgoal.service;

import com.modoo.application.common.dto.periodgoal.response.PeriodGoalSummary;
import com.modoo.application.common.port.periodgoal.in.RetrievePeriodGoalUseCase;
import com.modoo.application.common.port.periodgoal.out.PeriodGoalLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.PeriodGoalErrorCode;
import com.modoo.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.modoo.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.modoo.domain.planning.periodgoal.aggregate.vo.PeriodGoalDate;
import com.modoo.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrievePeriodGoalService implements RetrievePeriodGoalUseCase {

  private final PeriodGoalLoaderPort periodGoalLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public PeriodGoalSummary retrieve(Long userId, LocalDate date, String type) {
    User user = userLoaderPort.getUserOrElseThrow(
        userId,
        PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    PeriodGoalDate periodGoalDate = PeriodGoalDate.of(PeriodGoalType.from(type), date);

    Optional<PeriodGoal> periodGoal = periodGoalLoaderPort.getOptionalByDate(
        user.getId(),
        periodGoalDate.getDate(),
        PeriodGoalType.from(type)
    );

    return PeriodGoalSummary.ofNullable(periodGoal);
  }

}
