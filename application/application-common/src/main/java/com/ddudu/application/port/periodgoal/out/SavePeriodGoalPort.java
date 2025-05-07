package com.ddudu.application.port.periodgoal.out;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;

public interface SavePeriodGoalPort {

  PeriodGoal save(PeriodGoal periodGoal);

}
