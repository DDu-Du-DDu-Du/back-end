package com.ddudu.like.persistence.dao;

import com.ddudu.like.persistence.entity.LikeEntity;
import com.ddudu.todo.domain.Todo;
import java.util.List;

public interface LikeDaoCustom {

  List<LikeEntity> findByTodos(List<Todo> todos);

}
