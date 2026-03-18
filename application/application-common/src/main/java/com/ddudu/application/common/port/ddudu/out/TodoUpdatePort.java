package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.todo.aggregate.Todo;

public interface TodoUpdatePort {

  Todo update(Todo ddudu);

}
