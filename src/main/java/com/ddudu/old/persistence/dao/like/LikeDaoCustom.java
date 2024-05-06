package com.ddudu.old.persistence.dao.like;

import com.ddudu.old.persistence.entity.LikeEntity;
import com.ddudu.old.persistence.entity.TodoEntity;
import java.util.List;

public interface LikeDaoCustom {

  List<LikeEntity> findByTodos(List<TodoEntity> todos);

}
