package com.ddudu.todo.domain;

import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository {

  Todo save(Todo todo);

  Optional<Todo> findById(Long id);

  List<Todo> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, User user);

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, User user, List<PrivacyType> privacyTypes
  );

  void update(Todo todo);

}
