package com.ddudu.application.common.port.todo.out;

import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoLoaderPort {

  Todo getTodoOrElseThrow(Long id, String message);

  Optional<Todo> getOptionalTodo(Long id);

  List<Todo> getRepeatedTodos(RepeatTodo repeatTodo);

  List<Todo> getDailyTodos(LocalDate date, Long userId, List<PrivacyType> accessiblePrivacyTypes);

  int countTodayTodo(Long userId);

}
