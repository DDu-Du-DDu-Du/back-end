package com.ddudu.persistence.repository;

import com.ddudu.persistence.dao.user.FollowingDao;
import com.ddudu.persistence.entity.FollowingEntity;
import com.ddudu.persistence.entity.UserEntity;
import com.ddudu.application.user.domain.Following;
import com.ddudu.application.user.domain.FollowingRepository;
import com.ddudu.application.user.domain.User;
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
    return followingDao.findById(id)
        .map(FollowingEntity::toDomain);
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
