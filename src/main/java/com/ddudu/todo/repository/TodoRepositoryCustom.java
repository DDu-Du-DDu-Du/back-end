package com.ddudu.todo.repository;

import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepositoryCustom {

  List<Todo> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate);

  List<TodoCompletionResponse> findTodosCompletion(LocalDateTime startDate, LocalDateTime endDate);

}