package com.ddudu.infrastructure.persistence.repository.token;

import static com.ddudu.infrastructure.persistence.entity.QRefreshTokenEntity.refreshTokenEntity;

import com.ddudu.infrastructure.persistence.entity.RefreshTokenEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenQueryRepositoryImpl implements RefreshTokenQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<RefreshTokenEntity> getLastFamilyByUserId(Long userId) {
    return jpaQueryFactory.selectFrom(refreshTokenEntity)
        .where(refreshTokenEntity.userId.eq(userId))
        .orderBy(refreshTokenEntity.family.desc())
        .limit(1)
        .stream()
        .findFirst();
  }

  @Override
  public List<RefreshTokenEntity> findAllByUserFamily(Long userId, int family) {
    BooleanBuilder whereCondition = new BooleanBuilder(refreshTokenEntity.userId.eq(userId))
        .and(refreshTokenEntity.family.eq(family));

    return jpaQueryFactory.selectFrom(refreshTokenEntity)
        .where(whereCondition)
        .orderBy(refreshTokenEntity.id.desc())
        .fetch();
  }

}
