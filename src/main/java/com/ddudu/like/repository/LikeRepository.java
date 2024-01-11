package com.ddudu.like.repository;

import com.ddudu.like.domain.Like;
import com.ddudu.todo.domain.Todo;
import com.ddudu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

  boolean existsByUserAndTodo(User user, Todo todo);

}
