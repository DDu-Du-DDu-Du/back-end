package com.modoo.application.common.port.goal.out;

import com.modoo.domain.planning.goal.aggregate.Goal;

public interface DeleteGoalPort {

  void deleteWithTodos(Goal goal);

}
