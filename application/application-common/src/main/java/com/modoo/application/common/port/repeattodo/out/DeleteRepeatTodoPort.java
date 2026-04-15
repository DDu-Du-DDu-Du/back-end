package com.modoo.application.common.port.repeattodo.out;

import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;

public interface DeleteRepeatTodoPort {

  void deleteWithTodos(RepeatTodo repeatTodo);

  void deleteAllWithTodosByGoal(Goal goal);

}
