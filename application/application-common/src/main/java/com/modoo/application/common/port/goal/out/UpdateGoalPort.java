package com.modoo.application.common.port.goal.out;

import com.modoo.domain.planning.goal.aggregate.Goal;

public interface UpdateGoalPort {

  Goal update(Goal goal);

}
