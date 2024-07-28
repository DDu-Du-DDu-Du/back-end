package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.dto.FullUser;
import com.ddudu.infrastructure.persistence.entity.AuthProviderEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.auth.AuthProviderRepository;
import com.ddudu.infrastructure.persistence.repository.user.UserRepository;
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
        .map(provider -> saveAuthProvider(provider, savedUser))
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

    User user = providerEntity.get()
        .getUser()
        .toDomain();

    return Optional.of(user);
  }

  @Override
  public Optional<User> loadFullUser(Long userId) {
    return userRepository.fetchFullUserById(userId)
        .map(FullUser::toDomain);
  }

  private AuthProvider saveAuthProvider(AuthProvider authProvider, UserEntity user) {
    AuthProviderEntity entity = AuthProviderEntity.from(authProvider, user);

    return authProviderRepository.save(entity)
        .toDomain();
  }

}
