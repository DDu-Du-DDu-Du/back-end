package com.ddudu.infra.mysql.user.user.adapter;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.infra.mysql.user.user.dto.FullUser;
import com.ddudu.infra.mysql.user.user.entity.AuthProviderEntity;
import com.ddudu.infra.mysql.user.user.entity.UserEntity;
import com.ddudu.infra.mysql.user.user.repository.AuthProviderRepository;
import com.ddudu.infra.mysql.user.user.repository.UserRepository;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserLoaderPort, SignUpPort {

  private final UserRepository userRepository;
  private final AuthProviderRepository authProviderRepository;

  @Override
  public User getUserOrElseThrow(Long id, String message) {
    return userRepository.findById(id)
        .map(UserEntity::toDomain)
        .orElseThrow(() -> new MissingResourceException(
            message,
            User.class.getName(),
            id.toString()
        ));
  }

  @Override
  public User save(User user) {
    List<AuthProvider> authProviders = user.getAuthProviders();
    UserEntity savedUser = userRepository.save(UserEntity.from(user));

    List<AuthProvider> savedProviders = authProviders.stream()
        .map(provider -> saveAuthProvider(provider, savedUser.getId()))
        .toList();

    return savedUser.toDomainWith(savedProviders);
  }

  @Override
  public Optional<User> loadSocialUser(AuthProvider authProvider) {
    Optional<AuthProviderEntity> providerEntity = authProviderRepository.findByProviderIdAndProviderType(
        authProvider.getProviderId(), authProvider.getProviderType());

    if (providerEntity.isEmpty()) {
      return Optional.empty();
    }

    Long userId = providerEntity.get()
        .getUserId();

    return userRepository.findById(userId)
        .map(UserEntity::toDomain);
  }

  @Override
  public Optional<User> loadFullUser(Long userId) {
    return userRepository.fetchFullUserById(userId)
        .map(FullUser::toDomain);
  }

  private AuthProvider saveAuthProvider(AuthProvider authProvider, Long userId) {
    AuthProviderEntity entity = AuthProviderEntity.from(authProvider, userId);

    return authProviderRepository.save(entity)
        .toDomain();
  }

}
