package com.ddudu.application.port.periodgoal.out;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;

public interface UpdatePeriodGoalPort {

  PeriodGoal update(PeriodGoal periodGoal);

}
