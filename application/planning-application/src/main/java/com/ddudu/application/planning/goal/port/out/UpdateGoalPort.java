package com.ddudu.application.planning.goal.port.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;

public interface UpdateGoalPort {

  Goal update(Goal goal);

}
