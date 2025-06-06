package com.ddudu.infra.mysql.planning.repeatddudu.repository;

import static com.ddudu.infra.mysql.planning.repeatddudu.entity.QRepeatDduduEntity.repeatDduduEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RepeatDduduQueryRepositoryImpl implements RepeatDduduQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  @Override
  public void deleteAllByGoal(Long goalId) {
    jpaQueryFactory
        .delete(repeatDduduEntity)
        .where(repeatDduduEntity.goalId.eq(goalId))
        .execute();

    entityManager.flush();
    entityManager.clear();
  }

}
