package com.ddudu.old.user.domain;

import com.ddudu.application.domain.user.domain.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingRepository {

  boolean existsByFollowerAndFollowee(User follower, User followee);

  Following save(Following following);

  Optional<Following> findById(Long id);

  void update(Following following);

  void delete(Following following);

}
