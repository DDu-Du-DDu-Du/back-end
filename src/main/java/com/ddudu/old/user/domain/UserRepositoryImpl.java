package com.ddudu.old.user.domain;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.user.dto.FollowingSearchType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  @Override
  public boolean existsByOptionalUsername(String optionalUsername) {
    return false;
  }

  @Override
  public boolean existsByEmail(String email) {
    return false;
  }

  @Override
  public User save(User user) {
    return null;
  }

  @Override
  public Optional<User> findById(Long id) {
    return Optional.empty();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.empty();
  }

  @Override
  public List<User> findFromFollowingBySearchType(User user, FollowingSearchType searchType) {
    return List.of();
  }

  @Override
  public List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType searchType) {
    return List.of();
  }

  @Override
  public void update(User user) {

  }

  @Override
  public void delete(User user) {

  }

}
