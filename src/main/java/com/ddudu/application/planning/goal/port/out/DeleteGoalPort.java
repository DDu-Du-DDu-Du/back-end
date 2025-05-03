package com.ddudu.application.planning.goal.port.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;

public interface DeleteGoalPort {

  void deleteWithDdudus(Goal goal);

}
