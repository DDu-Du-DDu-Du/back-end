package com.ddudu.user.repository;

import static com.ddudu.persistence.entity.QFollowingEntity.followingEntity;
import static com.ddudu.persistence.entity.QUserEntity.userEntity;

import com.ddudu.persistence.entity.QUserEntity;
import com.ddudu.persistence.entity.UserEntity;
import com.ddudu.user.domain.FollowingStatus;
import com.ddudu.user.domain.UserSearchType;
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
  public List<UserEntity> findAllByKeywordAndSearchType(
      String keyword, UserSearchType userSearchType
  ) {
    BooleanBuilder whereClause = new BooleanBuilder();

    switch (userSearchType) {
      case EMAIL -> whereClause.and(userEntity.email.eq(keyword));
      case NICKNAME -> whereClause.and(userEntity.nickname.eq(keyword));
      case OPTIONAL_USERNAME -> whereClause.and(userEntity.optionalUsername.eq(keyword));
    }

    return jpaQueryFactory
        .selectFrom(userEntity)
        .where(whereClause)
        .fetch();
  }

  @Override
  public List<UserEntity> findFromFollowingBySearchType(
      UserEntity user, FollowingSearchType searchType
  ) {
    BooleanBuilder whereClause = new BooleanBuilder();
    QUserEntity owner = followingEntity.follower;
    QUserEntity target = followingEntity.followee;

    if (FollowingSearchType.isSearchingFollower(searchType)) {
      owner = followingEntity.followee;
      target = followingEntity.follower;
    }

    whereClause.and(owner.eq(user))
        .and(followingEntity.status.eq(FollowingStatus.FOLLOWING));

    return jpaQueryFactory
        .select(target)
        .from(followingEntity)
        .where(whereClause)
        .fetch();
  }

}
