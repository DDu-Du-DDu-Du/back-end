package com.ddudu.old.persistence.dao.like;

import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.old.persistence.entity.LikeEntity;
import java.util.List;

public interface LikeDaoCustom {

  List<LikeEntity> findByTodos(List<DduduEntity> todos);

}
