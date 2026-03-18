package com.ddudu.application.common.port.goal.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;

public interface DeleteGoalPort {

  void deleteWithTodos(Goal goal);

}
