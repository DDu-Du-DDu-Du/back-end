package com.ddudu.application.port.out.repeat_ddudu;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;

public interface DeleteRepeatDduduPort {

  void deleteWithDdudus(RepeatDdudu repeatDdudu);

  void deleteAllWithDdudusByGoal(Goal goal);

}
