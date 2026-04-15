package com.modoo.application.common.port.goal.out;

import com.modoo.domain.planning.goal.aggregate.Goal;
import java.util.List;

public interface SaveGoalPort {

  Goal save(Goal goal);

  List<Goal> saveAll(List<Goal> defaultGoals);

}
