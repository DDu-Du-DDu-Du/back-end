package com.ddudu.old.persistence.repository;

import static java.util.Objects.isNull;

import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.persistence.dao.like.LikeDao;
import com.ddudu.old.persistence.entity.LikeEntity;
import com.ddudu.old.persistence.entity.TodoEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.todo.domain.Todo;
import com.ddudu.application.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

  private final LikeDao likeDao;

  @Override
  public Like save(Like like) {
    return likeDao.save(LikeEntity.from(like))
        .toDomain();
  }

  @Override
  public Optional<Like> findById(Long id) {
    return likeDao.findById(id)
        .map(LikeEntity::toDomain);
  }

  @Override
  public Like findByUserAndTodo(User user, Todo todo) {
    LikeEntity found = likeDao.findByUserAndTodo(
        UserEntity.from(user), TodoEntity.from(todo));

    if (isNull(found)) {
      return null;
    }

    return found.toDomain();
  }

  @Override
  public List<Like> findByTodos(List<Todo> todos) {
    List<TodoEntity> todoEntities = todos.stream()
        .map(TodoEntity::from)
        .toList();

    return likeDao.findByTodos(todoEntities)
        .stream()
        .map(LikeEntity::toDomain)
        .toList();
  }

  @Override
  public void update(Like like) {
    likeDao.save(LikeEntity.from(like));
  }

  @Override
  public void delete(Like like) {
    likeDao.delete(LikeEntity.from(like));
  }

}