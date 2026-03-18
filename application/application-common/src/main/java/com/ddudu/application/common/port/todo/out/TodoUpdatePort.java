package com.ddudu.application.common.port.todo.out;

import com.ddudu.domain.planning.todo.aggregate.Todo;

public interface TodoUpdatePort {

  Todo update(Todo todo);

}
