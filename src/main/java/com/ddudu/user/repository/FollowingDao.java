package com.ddudu.user.repository;

import com.ddudu.persistence.entity.FollowingEntity;
import com.ddudu.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingDao extends JpaRepository<FollowingEntity, Long> {

  boolean existsByFollowerAndFollowee(UserEntity follower, UserEntity followee);

}
