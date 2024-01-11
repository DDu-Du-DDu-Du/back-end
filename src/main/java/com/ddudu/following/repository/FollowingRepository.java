package com.ddudu.following.repository;

import com.ddudu.following.domain.Following;
import com.ddudu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> {

  boolean existsByFollowerAndFollowee(User follower, User followee);

}
