package com.modoo.application.common.port.repeattodo.out;

import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import java.util.List;
import java.util.Optional;

public interface RepeatTodoLoaderPort {

  Optional<RepeatTodo> getOptionalRepeatTodo(Long id);

  List<RepeatTodo> getAllByGoal(Goal goal);

  RepeatTodo getOrElseThrow(Long id, String message);

}
