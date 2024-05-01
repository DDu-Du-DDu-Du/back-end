package com.ddudu.goal.repository;

import static com.ddudu.persistence.entity.QGoalEntity.goalEntity;

import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.persistence.entity.GoalEntity;
import com.ddudu.persistence.entity.UserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class GoalRepositoryImpl implements GoalRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public GoalRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

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
