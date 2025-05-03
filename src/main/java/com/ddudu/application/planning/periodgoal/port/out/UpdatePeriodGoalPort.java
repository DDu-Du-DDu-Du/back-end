package com.ddudu.application.planning.periodgoal.port.out;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;

public interface UpdatePeriodGoalPort {

  PeriodGoal update(PeriodGoal periodGoal);

}
