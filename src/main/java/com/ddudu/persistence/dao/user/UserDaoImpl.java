package com.ddudu.persistence.dao.user;

import static com.ddudu.persistence.entity.QFollowingEntity.followingEntity;
import static com.ddudu.persistence.entity.QUserEntity.userEntity;

import com.ddudu.application.user.domain.FollowingStatus;
import com.ddudu.application.user.domain.UserSearchType;
import com.ddudu.application.user.dto.FollowingSearchType;
import com.ddudu.persistence.entity.QUserEntity;
import com.ddudu.persistence.entity.UserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDaoCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public UserDaoImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<UserEntity> findAllByKeywordAndSearchType(
      String keyword, UserSearchType userSearchType
  ) {
    BooleanBuilder whereClause = new BooleanBuilder();

    switch (userSearchType) {
      case NICKNAME -> whereClause.and(userEntity.nickname.eq(keyword));
      case OPTIONAL_USERNAME -> whereClause.and(userEntity.username.eq(keyword));
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
    QUserEntity owner = followingEntity.id.follower;
    QUserEntity target = followingEntity.id.followee;

    if (FollowingSearchType.isSearchingFollower(searchType)) {
      owner = followingEntity.id.followee;
      target = followingEntity.id.follower;
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
