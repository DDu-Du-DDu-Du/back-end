package com.ddudu.user.repository;

import static com.ddudu.following.domain.QFollowing.following;
import static com.ddudu.user.domain.QUser.user;

import com.ddudu.following.domain.FollowingStatus;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.UserSearchType;
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
  public List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType) {
    BooleanBuilder whereClause = new BooleanBuilder();

    switch (userSearchType) {
      case EMAIL -> whereClause.and(user.email.address.eq(keyword));
      case NICKNAME -> whereClause.and(user.nickname.eq(keyword));
      case OPTIONAL_USERNAME -> whereClause.and(user.optionalUsername.eq(keyword));
    }

    return jpaQueryFactory
        .selectFrom(user)
        .where(whereClause)
        .fetch();
  }

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
