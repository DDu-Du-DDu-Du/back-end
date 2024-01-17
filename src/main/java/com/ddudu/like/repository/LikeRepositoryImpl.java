package com.ddudu.like.repository;

import static com.ddudu.like.domain.QLike.like;

import com.ddudu.like.domain.Like;
import com.ddudu.todo.domain.Todo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepositoryImpl implements LikeRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public LikeRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<Like> findByTodos(List<Todo> todos) {
    return jpaQueryFactory
        .selectFrom(like)
        .where(
            like.todo.in(todos),
            like.isDeleted.eq(false)
        )
        .fetch();
  }

}
