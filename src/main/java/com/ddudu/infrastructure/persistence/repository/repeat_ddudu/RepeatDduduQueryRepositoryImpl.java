package com.ddudu.infrastructure.persistence.repository.repeat_ddudu;

import static com.ddudu.infrastructure.persistence.entity.QRepeatDduduEntity.repeatDduduEntity;

import com.ddudu.infrastructure.persistence.entity.GoalEntity;
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
  public void deleteAllByGoal(GoalEntity goal) {
    jpaQueryFactory
        .delete(repeatDduduEntity)
        .where(repeatDduduEntity.goal.eq(goal))
        .execute();

    entityManager.clear();
  }

}
