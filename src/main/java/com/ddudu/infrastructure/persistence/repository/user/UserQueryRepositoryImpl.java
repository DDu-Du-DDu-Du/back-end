package com.ddudu.infrastructure.persistence.repository.user;

import static com.ddudu.infrastructure.persistence.entity.QAuthProviderEntity.authProviderEntity;
import static com.ddudu.infrastructure.persistence.entity.QUserEntity.userEntity;
import static com.ddudu.old.persistence.entity.QFollowingEntity.followingEntity;

import com.ddudu.infrastructure.persistence.dto.FullUser;
import com.ddudu.infrastructure.persistence.entity.QUserEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.user.domain.FollowingStatus;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

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

  @Override
  public Optional<FullUser> fetchFullUserById(Long id) {
    return jpaQueryFactory.select(userEntity, authProviderEntity)
        .from(userEntity)
        .join(authProviderEntity)
        .on(authProviderEntity.user.eq(userEntity))
        .where(userEntity.id.eq(id))
        .transform(
            GroupBy.groupBy(userEntity)
                .as(GroupBy.list(authProviderEntity))
        )
        .entrySet()
        .stream()
        .map(entry -> new FullUser(entry.getKey(), entry.getValue()))
        .findFirst();
  }

}
