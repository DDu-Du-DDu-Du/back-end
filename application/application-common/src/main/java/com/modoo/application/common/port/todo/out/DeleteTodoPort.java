package com.modoo.application.common.port.todo.out;

import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.todo.aggregate.Todo;

public interface DeleteTodoPort {

  void delete(Todo todo);

  void deleteAllByRepeatTodo(RepeatTodo repeatTodo);

}
