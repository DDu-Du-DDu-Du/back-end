package com.ddudu.like.repository;

import com.ddudu.persistence.entity.LikeEntity;
import com.ddudu.persistence.entity.TodoEntity;
import com.ddudu.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeDao extends JpaRepository<LikeEntity, Long>, LikeDaoCustom {

  LikeEntity findByUserAndTodo(UserEntity user, TodoEntity todo);

}
