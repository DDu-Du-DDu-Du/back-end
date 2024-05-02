package com.ddudu.persistence.repository;

import com.ddudu.application.user.domain.User;
import com.ddudu.application.user.domain.UserRepository;
import com.ddudu.application.user.domain.UserSearchType;
import com.ddudu.application.user.dto.FollowingSearchType;
import com.ddudu.persistence.dao.user.UserDao;
import com.ddudu.persistence.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserDao userDao;

  @Override
  public boolean existsByOptionalUsername(String optionalUsername) {
    return userDao.existsByOptionalUsername(optionalUsername);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userDao.existsByEmail(email);
  }

  @Override
  public User save(User user) {
    return userDao.save(UserEntity.from(user))
        .toDomain();
  }

  @Override
  public Optional<User> findById(Long id) {
    return userDao.findById(id)
        .map(UserEntity::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userDao.findByEmail(email)
        .map(UserEntity::toDomain);
  }

  @Override
  public List<User> findFromFollowingBySearchType(User user, FollowingSearchType searchType) {
    return userDao.findFromFollowingBySearchType(UserEntity.from(user), searchType)
        .stream()
        .map(UserEntity::toDomain)
        .toList();
  }

  @Override
  public List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType searchType) {
    return userDao.findAllByKeywordAndSearchType(keyword, searchType)
        .stream()
        .map(UserEntity::toDomain)
        .toList();
  }

  @Override
  public void update(User user) {
    userDao.save(UserEntity.from(user));
  }

  @Override
  public void delete(User user) {
    userDao.delete(UserEntity.from(user));
  }

}
