package com.ddudu.application.planning.repeatddudu.port.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;

public interface DeleteRepeatDduduPort {
  
  void deleteWithDdudus(RepeatDdudu repeatDdudu);

  void deleteAllWithDdudusByGoal(Goal goal);

}
