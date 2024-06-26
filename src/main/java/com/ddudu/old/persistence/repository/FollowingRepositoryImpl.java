package com.ddudu.old.persistence.repository;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.persistence.dao.user.FollowingDao;
import com.ddudu.old.persistence.entity.FollowingEntity;
import com.ddudu.old.user.domain.Following;
import com.ddudu.old.user.domain.FollowingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowingRepositoryImpl implements FollowingRepository {

  private final FollowingDao followingDao;

  @Override
  public boolean existsByFollowerAndFollowee(User follower, User followee) {
    return followingDao.existsByFollowerAndFollowee(
        UserEntity.from(follower), UserEntity.from(followee));
  }

  @Override
  public Following save(Following following) {
    return followingDao.save(FollowingEntity.from(following))
        .toDomain();
  }

  @Override
  public Optional<Following> findById(Long id) {
    // DO NOTHING
    return Optional.empty();
  }

  @Override
  public void delete(Following following) {
    followingDao.delete(FollowingEntity.from(following));
  }

  @Override
  public void update(Following following) {
    followingDao.save(FollowingEntity.from(following));
  }

}
