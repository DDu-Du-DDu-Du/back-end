package com.ddudu.old.persistence.repository;

import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.user.UserRepository;
import com.ddudu.old.user.domain.User;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements com.ddudu.old.user.domain.UserRepository {

  private final UserRepository userRepository;

  @Override
  public boolean existsByOptionalUsername(String optionalUsername) {
    // DO NOTHING
    return false;
  }

  @Override
  public boolean existsByEmail(String email) {
    // DO NOTHING
    return false;
  }

  @Override
  public User save(User user) {
    return userRepository.save(UserEntity.from(user))
        .toDomain();
  }

  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id)
        .map(UserEntity::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    // DO NOTHING
    return Optional.empty();
  }

  @Override
  public List<User> findFromFollowingBySearchType(User user, FollowingSearchType searchType) {
    return userRepository.findFromFollowingBySearchType(UserEntity.from(user), searchType)
        .stream()
        .map(UserEntity::toDomain)
        .toList();
  }

  @Override
  public List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType searchType) {
    return userRepository.findAllByKeywordAndSearchType(keyword, searchType)
        .stream()
        .map(UserEntity::toDomain)
        .toList();
  }

  @Override
  public void update(User user) {
    userRepository.save(UserEntity.from(user));
  }

  @Override
  public void delete(User user) {
    userRepository.delete(UserEntity.from(user));
  }

}
