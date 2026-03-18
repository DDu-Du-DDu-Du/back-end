package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.todo.aggregate.Todo;

public interface RepeatTodoPort {

  Todo getTodoOrElseThrow(Long id, String message);

  Todo save(Todo ddudu);

}
