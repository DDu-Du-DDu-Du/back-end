package com.ddudu.infrastructure.persistence.repository.repeat_ddudu;

import com.ddudu.infrastructure.persistence.entity.GoalEntity;

public interface RepeatDduduQueryRepository {

  void deleteAllByGoal(GoalEntity goal);

}
