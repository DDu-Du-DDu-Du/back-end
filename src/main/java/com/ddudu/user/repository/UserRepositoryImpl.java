package com.ddudu.user.repository;

import static com.ddudu.following.domain.QFollowing.following;

import com.ddudu.user.domain.User;
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
    return jpaQueryFactory
        .select(following.followee)
        .from(following)
        .where(following.follower.eq(follower))
        .fetch();
  }

}
