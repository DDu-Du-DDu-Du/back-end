package com.ddudu.old.persistence.repository;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import com.ddudu.old.persistence.entity.TodoEntity;
import com.ddudu.old.todo.domain.Todo;
import com.ddudu.old.todo.domain.TodoRepository;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepository {

  private final DduduRepository dduduRepository;

  @Override
  public Todo save(Todo todo) {
    return dduduRepository.save(TodoEntity.from(todo))
        .toDomain();
  }

  @Override
  public Optional<Todo> findById(Long id) {
    return dduduRepository.findById(id)
        .map(TodoEntity::toDomain);
  }

  @Override
  public List<Todo> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, User user
  ) {
    return dduduRepository.findTodosByDate(startDate, endDate, UserEntity.from(user))
        .stream()
        .map(TodoEntity::toDomain)
        .toList();
  }

  @Override
  public List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, User user, List<PrivacyType> privacyTypes
  ) {
    return dduduRepository.findTodosCompletion(
        startDate, endDate, UserEntity.from(user), privacyTypes);
  }

  @Override
  public void update(Todo todo) {
    dduduRepository.save(TodoEntity.from(todo));
  }

  @Override
  public void delete(Todo todo) {
    dduduRepository.delete(TodoEntity.from(todo));
  }

}
