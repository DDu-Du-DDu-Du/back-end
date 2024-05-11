package com.ddudu.old.persistence.dao.like;

import static com.ddudu.old.persistence.entity.QLikeEntity.likeEntity;

import com.ddudu.old.persistence.entity.LikeEntity;
import com.ddudu.old.persistence.entity.TodoEntity;
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
  public List<LikeEntity> findByTodos(List<TodoEntity> todos) {
    return jpaQueryFactory
        .selectFrom(likeEntity)
        .where(
            likeEntity.todo.in(todos)
        )
        .fetch();
  }

}
