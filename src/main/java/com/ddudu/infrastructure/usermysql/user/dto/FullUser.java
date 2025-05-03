package com.ddudu.infrastructure.usermysql.user.dto;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.infrastructure.usermysql.user.entity.AuthProviderEntity;
import com.ddudu.infrastructure.usermysql.user.entity.UserEntity;
import java.util.List;

public record FullUser(UserEntity userEntity, List<AuthProviderEntity> authProviderEntities) {

  public User toDomain() {
    List<AuthProvider> authProviders = authProviderEntities.stream()
        .map(AuthProviderEntity::toDomain)
        .toList();

    return userEntity.toDomainWith(authProviders);
  }

}
