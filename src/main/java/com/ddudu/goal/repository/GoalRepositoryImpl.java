package com.ddudu.goal.repository;

import static com.ddudu.goal.domain.QGoal.goal;

import com.ddudu.goal.domain.Goal;
import com.ddudu.user.domain.User;
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
        .where(
            goal.user.eq(user),
            goal.isDeleted.eq(false)
        )
        .orderBy(goal.status.desc(), goal.createdAt.asc())
        .fetch();
  }

}
