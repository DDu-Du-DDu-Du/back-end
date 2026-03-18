package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;

public interface DeleteTodoPort {

  void delete(Todo ddudu);

  void deleteAllByRepeatTodo(RepeatTodo repeatTodo);

}
