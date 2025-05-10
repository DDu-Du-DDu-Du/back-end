package com.ddudu.application.common.port.periodgoal.out;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;

public interface UpdatePeriodGoalPort {

  PeriodGoal update(PeriodGoal periodGoal);

}
