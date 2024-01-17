package com.ddudu.user.repository;

import static com.ddudu.user.domain.QFollowing.following;

import com.ddudu.user.domain.FollowingStatus;
import com.ddudu.user.domain.QUser;
import com.ddudu.user.domain.User;
import com.ddudu.user.dto.FollowingSearchType;
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
  public List<User> findFromFollowingBySearchType(User user, FollowingSearchType searchType) {
    BooleanBuilder whereClause = new BooleanBuilder();
    QUser owner = following.follower;
    QUser target = following.followee;

    if (FollowingSearchType.isSearchingFollower(searchType)) {
      owner = following.followee;
      target = following.follower;
    }

    whereClause.and(owner.eq(user))
        .and(following.status.eq(FollowingStatus.FOLLOWING));

    return jpaQueryFactory
        .select(target)
        .from(following)
        .where(whereClause)
        .fetch();
  }

}
