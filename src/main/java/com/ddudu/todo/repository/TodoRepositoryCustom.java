package com.ddudu.todo.repository;

import com.ddudu.todo.domain.Todo;
import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepositoryCustom {
  
  List<Todo> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate);

}
