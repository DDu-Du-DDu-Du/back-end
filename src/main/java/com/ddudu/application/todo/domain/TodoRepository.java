package com.ddudu.application.todo.domain;

import com.ddudu.application.goal.domain.PrivacyType;
import com.ddudu.application.todo.dto.response.TodoCompletionResponse;
import com.ddudu.application.user.domain.User;
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

  void delete(Todo todo);

}
