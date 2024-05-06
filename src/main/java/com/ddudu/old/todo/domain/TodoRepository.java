package com.ddudu.old.todo.domain;

import com.ddudu.old.goal.domain.PrivacyType;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.user.domain.User;
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
