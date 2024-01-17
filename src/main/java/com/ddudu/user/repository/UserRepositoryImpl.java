package com.ddudu.user.repository;

import static com.ddudu.user.domain.QFollowing.following;

import com.ddudu.user.domain.FollowingStatus;
import com.ddudu.user.domain.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public UserRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<User> findFolloweesOfUser(User follower) {
    BooleanBuilder whereClause = new BooleanBuilder();

    whereClause.and(following.follower.eq(follower))
        .and(following.status.eq(FollowingStatus.FOLLOWING));

    return jpaQueryFactory
        .select(following.followee)
        .from(following)
        .where(whereClause)
        .fetch();
  }

}
