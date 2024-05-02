package com.ddudu.persistence.dao.like;

import com.ddudu.persistence.entity.LikeEntity;
import com.ddudu.persistence.entity.TodoEntity;
import java.util.List;

public interface LikeDaoCustom {

  List<LikeEntity> findByTodos(List<TodoEntity> todos);

}
