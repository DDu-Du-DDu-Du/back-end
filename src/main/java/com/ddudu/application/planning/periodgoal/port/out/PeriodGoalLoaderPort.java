package com.ddudu.application.planning.periodgoal.port.out;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.util.Optional;

public interface PeriodGoalLoaderPort {

  PeriodGoal getOrElseThrow(Long id, String message);

  Optional<PeriodGoal> getOptionalByDate(User user, LocalDate date, PeriodGoalType type);

}
