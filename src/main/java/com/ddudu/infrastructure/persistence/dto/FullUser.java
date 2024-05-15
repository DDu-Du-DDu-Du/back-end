package com.ddudu.infrastructure.persistence.dto;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import com.ddudu.infrastructure.persistence.entity.AuthProviderEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import java.util.List;

public record FullUser(UserEntity userEntity, List<AuthProviderEntity> authProviderEntities) {

  public User toDomain() {
    List<AuthProvider> authProviders = authProviderEntities.stream()
        .map(AuthProviderEntity::toDomain)
        .toList();

    return userEntity.toDomainWith(authProviders);
  }

}
