package com.ddudu.old.like.domain;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.todo.domain.Todo;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository {

  Like save(Like like);

  Optional<Like> findById(Long id);

  Like findByUserAndTodo(User user, Todo todo);

  List<Like> findByTodos(List<Todo> todos);

  void update(Like like);

  void delete(Like like);

}
