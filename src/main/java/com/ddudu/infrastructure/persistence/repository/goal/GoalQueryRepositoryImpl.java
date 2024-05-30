package com.ddudu.infrastructure.persistence.repository.goal;

import static com.ddudu.infrastructure.persistence.entity.QDduduEntity.dduduEntity;
import static com.ddudu.infrastructure.persistence.entity.QGoalEntity.goalEntity;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.dto.goal.response.CompletedDduduNumberStatsResponse;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
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

  @Override
  public List<CompletedDduduNumberStatsResponse> collectCompletedDduduNumberStats(
      UserEntity user, LocalDate from, LocalDate to
  ) {
    StringPath completedCountAlias = Expressions.stringPath("completedCount");
    NumberExpression<Integer> sum = new CaseBuilder().when(
            dduduEntity.status.eq(DduduStatus.COMPLETE))
        .then(Expressions.numberTemplate(Integer.class, "1"))
        .otherwise(Expressions.numberTemplate(Integer.class, "0"))
        .sum()
        .as("completedCount");

    return jpaQueryFactory.select(Projections.constructor(
            CompletedDduduNumberStatsResponse.class,
            goalEntity.id,
            goalEntity.name,
            sum
        ))
        .from(goalEntity)
        .join(dduduEntity)
        .on(dduduEntity.goal.eq(goalEntity))
        .where(goalEntity.user.eq(user)
            .and(dduduEntity.scheduledOn.between(from, to)))
        .groupBy(goalEntity.id)
        .orderBy(completedCountAlias.desc())
        .fetch();
  }

}
