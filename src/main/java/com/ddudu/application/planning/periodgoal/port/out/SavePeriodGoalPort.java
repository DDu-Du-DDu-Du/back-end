package com.ddudu.application.planning.periodgoal.port.out;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;

public interface SavePeriodGoalPort {

  PeriodGoal save(PeriodGoal periodGoal);

}
