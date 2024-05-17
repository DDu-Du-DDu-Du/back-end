package com.ddudu.old.persistence.dao.like;

import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.persistence.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeDao extends JpaRepository<LikeEntity, Long>, LikeDaoCustom {

  LikeEntity findByUserAndTodo(UserEntity user, DduduEntity todo);

}
