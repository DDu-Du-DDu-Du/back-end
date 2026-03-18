package com.ddudu.application.common.port.todo.out;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import java.util.List;

public interface SaveTodoPort {

  Todo save(Todo todo);

  List<Todo> saveAll(List<Todo> todoList);

}
