package com.ddudu.persistence.dao.user;

import com.ddudu.persistence.entity.FollowingEntity;
import com.ddudu.persistence.entity.FollowingId;
import com.ddudu.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingDao extends JpaRepository<FollowingEntity, Long> {

  boolean existsById(FollowingId id);

  default boolean existsByFollowerAndFollowee(UserEntity follower, UserEntity followee) {
    return existsById(new FollowingId(follower, followee));
  }

}
