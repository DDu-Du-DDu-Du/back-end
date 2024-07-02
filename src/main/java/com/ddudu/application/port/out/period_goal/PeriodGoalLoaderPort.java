package com.ddudu.application.port.out.period_goal;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.user.domain.User;
import java.time.LocalDate;
import java.util.Optional;

public interface PeriodGoalLoaderPort {

  PeriodGoal getOrElseThrow(Long id, String message);

  Optional<PeriodGoal> getOptionalByDate(User user, LocalDate date, PeriodGoalType type);

}
