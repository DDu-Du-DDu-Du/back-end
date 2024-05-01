package com.ddudu.like.persistence.dao;

import com.ddudu.like.domain.Like;
import com.ddudu.like.persistence.entity.LikeEntity;
import com.ddudu.todo.domain.Todo;
import com.ddudu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeDao extends JpaRepository<LikeEntity, Long>, LikeDaoCustom {

  LikeEntity findByUserAndTodo(User user, Todo todo);

  LikeEntity save(Like like);

}
