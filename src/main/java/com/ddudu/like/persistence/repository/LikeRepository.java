package com.ddudu.like.persistence.repository;

import static java.util.Objects.isNull;

import com.ddudu.like.domain.Like;
import com.ddudu.like.persistence.dao.LikeDao;
import com.ddudu.like.persistence.entity.LikeEntity;
import com.ddudu.todo.domain.Todo;
import com.ddudu.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

  private final LikeDao likeDao;

  public Like findByUserAndTodo(User user, Todo todo) {
    if (isNull(likeDao.findByUserAndTodo(user, todo))) {
      return null;
    }

    return likeDao.findByUserAndTodo(user, todo)
        .toDomain();
  }

  public Like save(Like like) {
    return likeDao.save(LikeEntity.from(like))
        .toDomain();
  }

  public List<Like> findByTodos(List<Todo> todos) {
    return likeDao.findByTodos(todos)
        .stream()
        .map(LikeEntity::toDomain)
        .toList();
  }

  public Optional<Like> findById(Long id) {
    return likeDao.findById(id)
        .map(LikeEntity::toDomain);
  }

  public void update(Like like) {
    LikeEntity entity = likeDao.findById(like.getId())
        .orElseThrow();

    entity.update(like);
  }

}
