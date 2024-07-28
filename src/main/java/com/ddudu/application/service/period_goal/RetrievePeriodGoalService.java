package com.ddudu.application.service.period_goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.period_goal.domain.vo.PeriodGoalDate;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.period_goal.response.PeriodGoalSummary;
import com.ddudu.application.port.in.period_goal.RetrievePeriodGoalUseCase;
import com.ddudu.application.port.out.period_goal.PeriodGoalLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
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
        user,
        periodGoalDate.getDate(),
        PeriodGoalType.from(type)
    );

    return PeriodGoalSummary.ofNullable(periodGoal);
  }

}
