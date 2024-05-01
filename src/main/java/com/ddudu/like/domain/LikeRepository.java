package com.ddudu.like.domain;

import com.ddudu.todo.domain.Todo;
import com.ddudu.user.domain.User;
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

}
