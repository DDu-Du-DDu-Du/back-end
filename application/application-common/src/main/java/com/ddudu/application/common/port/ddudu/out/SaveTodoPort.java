package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import java.util.List;

public interface SaveTodoPort {

  Todo save(Todo ddudu);

  List<Todo> saveAll(List<Todo> dduduList);

}
