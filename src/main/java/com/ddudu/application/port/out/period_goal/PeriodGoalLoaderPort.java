package com.ddudu.application.port.out.period_goal;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;

public interface PeriodGoalLoaderPort {

  PeriodGoal getOrElseThrow(Long id, String message);

}
