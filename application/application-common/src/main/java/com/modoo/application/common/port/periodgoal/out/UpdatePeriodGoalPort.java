package com.modoo.application.common.port.periodgoal.out;

import com.modoo.domain.planning.periodgoal.aggregate.PeriodGoal;

public interface UpdatePeriodGoalPort {

  PeriodGoal update(PeriodGoal periodGoal);

}
