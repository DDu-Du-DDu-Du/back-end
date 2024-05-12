package com.ddudu.application.port.out;

import com.ddudu.application.domain.goal.domain.Goal;

public interface DeleteGoalPort {

  void delete(Goal goal);

}
