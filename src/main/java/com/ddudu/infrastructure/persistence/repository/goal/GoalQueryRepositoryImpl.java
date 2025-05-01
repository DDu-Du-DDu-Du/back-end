package com.ddudu.infrastructure.persistence.repository.goal;

import static com.ddudu.infrastructure.persistence.entity.QGoalEntity.goalEntity;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GoalQueryRepositoryImpl implements GoalQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<GoalEntity> findAllByUserId(Long userId) {
    return jpaQueryFactory
        .selectFrom(goalEntity)
        .where(goalEntity.userId.eq(userId))
        .orderBy(
            goalEntity.status.desc(),
            goalEntity.id.desc()
        )
        .fetch();
  }

  @Override
  public List<GoalEntity> findAllByUserAndPrivacyTypes(
      Long userId, List<PrivacyType> privacyTypes
  ) {
    BooleanBuilder whereClause = new BooleanBuilder();
    whereClause.and(goalEntity.userId.eq(userId));
    whereClause.and(goalEntity.privacyType.in(privacyTypes));

    return jpaQueryFactory
        .selectFrom(goalEntity)
        .where(whereClause)
        .fetch();
  }

}
