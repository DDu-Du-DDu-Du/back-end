package com.ddudu.persistence.repository;

import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.persistence.dao.todo.TodoDao;
import com.ddudu.persistence.entity.TodoEntity;
import com.ddudu.persistence.entity.UserEntity;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.domain.TodoRepository;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepository {

  private final TodoDao todoDao;

  @Override
  public Todo save(Todo todo) {
    return todoDao.save(TodoEntity.from(todo))
        .toDomain();
  }

  @Override
  public Optional<Todo> findById(Long id) {
    return todoDao.findById(id)
        .map(TodoEntity::toDomain);
  }

  @Override
  public List<Todo> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, User user
  ) {
    return todoDao.findTodosByDate(startDate, endDate, UserEntity.from(user))
        .stream()
        .map(TodoEntity::toDomain)
        .toList();
  }

  @Override
  public List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, User user, List<PrivacyType> privacyTypes
  ) {
    return todoDao.findTodosCompletion(startDate, endDate, UserEntity.from(user), privacyTypes);
  }

  @Override
  public void update(Todo todo) {
    todoDao.save(TodoEntity.from(todo));
  }

}
