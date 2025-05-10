package com.ddudu.application.common.port.goal.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import java.util.List;

public interface SaveGoalPort {

  Goal save(Goal goal);

  List<Goal> saveAll(List<Goal> defaultGoals);

}
