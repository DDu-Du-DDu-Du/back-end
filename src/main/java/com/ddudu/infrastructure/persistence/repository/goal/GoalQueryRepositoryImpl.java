package com.ddudu.infrastructure.persistence.repository.goal;

import static com.ddudu.infrastructure.persistence.entity.QGoalEntity.goalEntity;

import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
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
  public List<GoalEntity> findAllByUser(UserEntity user) {
    return jpaQueryFactory
        .selectFrom(goalEntity)
        .where(goalEntity.user.eq(user))
        .orderBy(
            goalEntity.status.desc(),
            goalEntity.id.desc()
        )
        .fetch();
  }

  @Override
  public List<GoalEntity> findAllByUserAndPrivacyTypes(
      UserEntity user, List<PrivacyType> privacyTypes
  ) {
    BooleanBuilder whereClause = new BooleanBuilder();
    whereClause.and(goalEntity.user.eq(user));
    whereClause.and(goalEntity.status.eq(GoalStatus.IN_PROGRESS));
    whereClause.and(goalEntity.privacyType.in(privacyTypes));

    return jpaQueryFactory
        .selectFrom(goalEntity)
        .where(whereClause)
        .fetch();
  }

}
