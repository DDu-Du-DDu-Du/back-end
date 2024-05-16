package com.ddudu.application.port.out;

import com.ddudu.application.domain.goal.domain.Goal;

public interface DeleteDduduPort {

  void deleteAllByGoal(Goal goal);

}
