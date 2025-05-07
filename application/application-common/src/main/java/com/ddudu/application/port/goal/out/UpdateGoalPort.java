package com.ddudu.application.port.goal.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;

public interface UpdateGoalPort {

  Goal update(Goal goal);

}
