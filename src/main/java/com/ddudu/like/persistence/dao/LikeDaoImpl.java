package com.ddudu.like.persistence.dao;

import static com.ddudu.like.persistence.entity.QLikeEntity.likeEntity;

import com.ddudu.like.persistence.entity.LikeEntity;
import com.ddudu.todo.domain.Todo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDaoImpl implements LikeDaoCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public LikeDaoImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<LikeEntity> findByTodos(List<Todo> todos) {
    return jpaQueryFactory
        .selectFrom(likeEntity)
        .where(
            likeEntity.todo.in(todos),
            likeEntity.isDeleted.eq(false)
        )
        .fetch();
  }

}
