package com.ddudu.infrastructure.persistence.repository.token;

import static com.ddudu.infrastructure.persistence.entity.QRefreshTokenEntity.refreshTokenEntity;

import com.ddudu.infrastructure.persistence.entity.RefreshTokenEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenQueryRepositoryImpl implements RefreshTokenQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public RefreshTokenQueryRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Optional<RefreshTokenEntity> getLastFamilyByUserId(Long userId) {
    return jpaQueryFactory.selectFrom(refreshTokenEntity)
        .where(refreshTokenEntity.refreshTokenId.userId.eq(userId))
        .orderBy(refreshTokenEntity.refreshTokenId.family.desc())
        .limit(1)
        .stream()
        .findFirst();
  }

}
