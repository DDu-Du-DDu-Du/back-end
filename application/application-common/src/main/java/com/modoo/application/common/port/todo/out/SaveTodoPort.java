package com.modoo.application.common.port.todo.out;

import com.modoo.domain.planning.todo.aggregate.Todo;
import java.util.List;

public interface SaveTodoPort {

  Todo save(Todo todo);

  List<Todo> saveAll(List<Todo> todoList);

}
