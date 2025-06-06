package com.ddudu.infra.mysql.user.user.repository;

import static com.ddudu.infra.mysql.user.user.entity.QAuthProviderEntity.authProviderEntity;
import static com.ddudu.infra.mysql.user.user.entity.QUserEntity.userEntity;

import com.ddudu.infra.mysql.user.user.dto.FullUser;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<FullUser> fetchFullUserById(Long id) {
    return jpaQueryFactory.select(userEntity, authProviderEntity)
        .from(userEntity)
        .join(authProviderEntity)
        .on(authProviderEntity.userId.eq(userEntity.id))
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
