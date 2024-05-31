package com.ddudu.application.port.out.goal;

import com.ddudu.application.domain.goal.domain.Goal;
import java.util.List;

public interface SaveGoalPort {

  Goal save(Goal goal);

  List<Goal> saveAll(List<Goal> defaultGoals);

}
