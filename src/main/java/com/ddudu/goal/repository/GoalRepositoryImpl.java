package com.ddudu.goal.repository;

import static com.ddudu.goal.domain.QGoal.goal;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.user.domain.User;
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
  public List<Goal> findAllByUser(User user) {
    return jpaQueryFactory
        .selectFrom(goal)
        .where(goal.user.eq(user))
        .orderBy(
            goal.status.desc(),
            goal.id.desc()
        )
        .fetch();
  }

  @Override
  public List<Goal> findAllByUserAndPrivacyType(User user, PrivacyType privacyType) {
    BooleanBuilder whereClause = new BooleanBuilder();
    whereClause.and(goal.user.eq(user));
    whereClause.and(goal.status.eq(GoalStatus.IN_PROGRESS));

    switch (privacyType) {
      case PUBLIC -> whereClause.and(goal.privacyType.eq(PrivacyType.PUBLIC));
      case FOLLOWER -> whereClause.and(goal.privacyType.ne(PrivacyType.PRIVATE));
      default -> {
      }
    }

    return jpaQueryFactory
        .selectFrom(goal)
        .where(whereClause)
        .fetch();
  }

}
