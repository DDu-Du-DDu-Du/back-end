package com.modoo.application.common.port.todo.out;

import com.modoo.domain.planning.todo.aggregate.Todo;

public interface RepeatTodoPort {

  Todo getTodoOrElseThrow(Long id, String message);

  Todo save(Todo todo);

}
