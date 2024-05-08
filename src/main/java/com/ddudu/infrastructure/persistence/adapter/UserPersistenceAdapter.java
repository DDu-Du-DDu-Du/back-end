package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.user.domain.AuthProvider;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.SignUpPort;
import com.ddudu.application.port.out.UserLoaderPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.AuthProviderEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.auth.AuthProviderRepository;
import com.ddudu.infrastructure.persistence.repository.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserLoaderPort, SignUpPort {

  private final UserRepository userRepository;
  private final AuthProviderRepository authProviderRepository;

  @Override
  public User create(User user) {
    return userRepository.save(UserEntity.from(user))
        .toDomain();
  }

  @Override
  public Optional<User> loadSocialUser(AuthProvider authProvider) {
    Optional<AuthProviderEntity> providerEntity = authProviderRepository.findByProviderIdAndProviderType(
        authProvider.getProviderId(), authProvider.getProviderType());

    if (providerEntity.isEmpty()) {
      return Optional.empty();
    }

    User user = providerEntity.get()
        .getUser()
        .toDomain();

    return Optional.of(user);
  }

}
