package com.ddudu.old.persistence.dao.user;

import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.persistence.entity.FollowingEntity;
import com.ddudu.old.persistence.entity.FollowingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingDao extends JpaRepository<FollowingEntity, Long> {

  boolean existsById(FollowingId id);

  default boolean existsByFollowerAndFollowee(UserEntity follower, UserEntity followee) {
    return existsById(new FollowingId(follower, followee));
  }

}
