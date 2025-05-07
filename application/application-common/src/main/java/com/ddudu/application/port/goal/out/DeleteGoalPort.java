package com.ddudu.application.port.goal.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;

public interface DeleteGoalPort {

  void deleteWithDdudus(Goal goal);

}
