package com.ddudu.infra.mysql.planning.repeattodo.repository;

import static com.ddudu.infra.mysql.planning.repeattodo.entity.QRepeatTodoEntity.repeatTodoEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RepeatTodoQueryRepositoryImpl implements RepeatTodoQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  @Override
  public void deleteAllByGoal(Long goalId) {
    jpaQueryFactory
        .delete(repeatTodoEntity)
        .where(repeatTodoEntity.goalId.eq(goalId))
        .execute();

    entityManager.flush();
    entityManager.clear();
  }

}
