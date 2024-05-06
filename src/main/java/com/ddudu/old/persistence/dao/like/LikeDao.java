package com.ddudu.old.persistence.dao.like;

import com.ddudu.old.persistence.entity.LikeEntity;
import com.ddudu.old.persistence.entity.TodoEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeDao extends JpaRepository<LikeEntity, Long>, LikeDaoCustom {

  LikeEntity findByUserAndTodo(UserEntity user, TodoEntity todo);

}
