package com.modoo.application.common.port.todo.out;

import com.modoo.domain.planning.todo.aggregate.Todo;

public interface TodoUpdatePort {

  Todo update(Todo todo);

}
