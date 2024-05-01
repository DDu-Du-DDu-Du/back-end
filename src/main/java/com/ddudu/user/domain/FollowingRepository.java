package com.ddudu.user.domain;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingRepository {

  boolean existsByFollowerAndFollowee(User follower, User followee);

  Following save(Following following);

  Optional<Following> findById(Long id);

  void delete(Following following);

  void update(Following following);

}
