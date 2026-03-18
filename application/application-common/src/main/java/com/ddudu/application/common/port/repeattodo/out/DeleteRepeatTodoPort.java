package com.ddudu.application.common.port.repeattodo.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;

public interface DeleteRepeatTodoPort {
  
  void deleteWithTodos(RepeatTodo repeatTodo);

  void deleteAllWithTodosByGoal(Goal goal);

}
