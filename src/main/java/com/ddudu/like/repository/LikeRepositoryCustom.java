package com.ddudu.like.repository;

import com.ddudu.persistence.entity.LikeEntity;
import com.ddudu.persistence.entity.TodoEntity;
import java.util.List;

public interface LikeRepositoryCustom {

  List<LikeEntity> findByTodos(List<TodoEntity> todos);

}
