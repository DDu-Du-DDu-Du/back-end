package com.ddudu.application.common.port.todo.out;

import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.todo.aggregate.Todo;

public interface DeleteTodoPort {

  void delete(Todo todo);

  void deleteAllByRepeatTodo(RepeatTodo repeatTodo);

}
