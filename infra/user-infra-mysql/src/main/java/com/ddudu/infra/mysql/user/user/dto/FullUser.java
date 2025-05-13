package com.ddudu.infra.mysql.user.user.dto;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.infra.mysql.user.user.entity.AuthProviderEntity;
import com.ddudu.infra.mysql.user.user.entity.UserEntity;
import java.util.List;

public record FullUser(UserEntity userEntity, List<AuthProviderEntity> authProviderEntities) {

  public User toDomain() {
    List<AuthProvider> authProviders = authProviderEntities.stream()
        .map(AuthProviderEntity::toDomain)
        .toList();

    return userEntity.toDomainWith(authProviders);
  }

}
