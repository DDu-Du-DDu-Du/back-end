package com.ddudu.like.repository;

import com.ddudu.like.domain.Like;
import com.ddudu.todo.domain.Todo;
import java.util.List;

public interface LikeRepositoryCustom {

  List<Like> findByTodos(List<Todo> todos);

}
