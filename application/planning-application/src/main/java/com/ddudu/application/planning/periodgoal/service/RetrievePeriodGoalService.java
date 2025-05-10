package com.ddudu.application.planning.periodgoal.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.domain.planning.periodgoal.aggregate.vo.PeriodGoalDate;
import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.common.dto.periodgoal.response.PeriodGoalSummary;
import com.ddudu.application.common.port.periodgoal.in.RetrievePeriodGoalUseCase;
import com.ddudu.application.common.port.periodgoal.out.PeriodGoalLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
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
        userId, PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());

    PeriodGoalDate periodGoalDate = PeriodGoalDate.of(PeriodGoalType.from(type), date);

    Optional<PeriodGoal> periodGoal = periodGoalLoaderPort.getOptionalByDate(
        user.getId(),
        periodGoalDate.getDate(),
        PeriodGoalType.from(type)
    );

    return PeriodGoalSummary.ofNullable(periodGoal);
  }

}
