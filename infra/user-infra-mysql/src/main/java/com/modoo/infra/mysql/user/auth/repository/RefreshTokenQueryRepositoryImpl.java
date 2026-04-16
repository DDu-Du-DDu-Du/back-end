package com.modoo.infra.mysql.user.auth.repository;

import static com.modoo.infra.mysql.user.auth.entiy.QRefreshTokenEntity.refreshTokenEntity;

import com.modoo.infra.mysql.user.auth.entiy.RefreshTokenEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
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

  @Override
  public Optional<RefreshTokenEntity> findByUserFamily(Long userId, int family) {
    BooleanBuilder whereCondition = new BooleanBuilder(refreshTokenEntity.userId.eq(userId))
        .and(refreshTokenEntity.family.eq(family));

    return Optional.ofNullable(
        jpaQueryFactory.selectFrom(refreshTokenEntity)
            .where(whereCondition)
            .fetchOne()
    );
  }

  @Override
  public void deleteByUserFamily(Long userId, int family) {
    BooleanBuilder whereCondition = new BooleanBuilder(refreshTokenEntity.userId.eq(userId))
        .and(refreshTokenEntity.family.eq(family));

    jpaQueryFactory.delete(refreshTokenEntity)
        .where(whereCondition)
        .execute();
  }

  @Override
  public long rotateIfCurrentMatches(
      Long userId,
      int family,
      String currentToken,
      String newCurrentToken,
      LocalDateTime refreshedAt
  ) {
    String previousToken = currentToken;
    BooleanBuilder whereCondition = new BooleanBuilder(refreshTokenEntity.userId.eq(userId))
        .and(refreshTokenEntity.family.eq(family))
        .and(refreshTokenEntity.currentToken.eq(currentToken));

    return jpaQueryFactory.update(refreshTokenEntity)
        .set(refreshTokenEntity.previousToken, previousToken)
        .set(refreshTokenEntity.currentToken, newCurrentToken)
        .set(refreshTokenEntity.refreshedAt, refreshedAt)
        .where(whereCondition)
        .execute();
  }

}
