package com.modoo.infra.mysql.planning.goal.repository;

import static com.modoo.infra.mysql.planning.goal.entity.QGoalEntity.goalEntity;

import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.infra.mysql.planning.goal.entity.GoalEntity;
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
      Long userId,
      List<PrivacyType> privacyTypes
  ) {
    BooleanBuilder whereClause = new BooleanBuilder();
    whereClause.and(goalEntity.userId.eq(userId));
    whereClause.and(goalEntity.privacyType.in(privacyTypes));

    return jpaQueryFactory
        .selectFrom(goalEntity)
        .where(whereClause)
        .fetch();
  }

  @Override
  public int findMaxPriorityByUserId(Long userId) {
    Integer maxPriority = jpaQueryFactory
        .select(goalEntity.priority.max())
        .from(goalEntity)
        .where(goalEntity.userId.eq(userId))
        .fetchOne();

    return maxPriority == null ? 0 : maxPriority;
  }

}
