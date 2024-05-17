package com.ddudu.application.port.out.goal;

import com.ddudu.application.domain.goal.domain.Goal;

public interface SaveGoalPort {

  Goal save(Goal goal);

}
