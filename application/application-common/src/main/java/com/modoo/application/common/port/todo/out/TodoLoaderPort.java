package com.modoo.application.common.port.todo.out;

import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.todo.aggregate.Todo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoLoaderPort {

  Todo getTodoOrElseThrow(Long id, String message);

  Optional<Todo> getOptionalTodo(Long id);

  List<Todo> getRepeatedTodos(RepeatTodo repeatTodo);

  List<Todo> getDailyTodos(LocalDate date, Long userId, List<PrivacyType> accessiblePrivacyTypes);

  List<Todo> getTodosBetween(
      LocalDateTime startAt,
      LocalDateTime endAt,
      Long userId,
      List<PrivacyType> accessiblePrivacyTypes
  );

  int countTodayTodo(Long userId);

  List<Todo> getTodosByUserId(Long userId);

}
