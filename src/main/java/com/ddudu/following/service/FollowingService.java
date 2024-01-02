package com.ddudu.following.service;

import com.ddudu.following.domain.Following;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.response.FollowResponse;
import com.ddudu.following.repository.FollowingRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowingService {

  private final UserRepository userRepository;
  private final FollowingRepository followingRepository;

  @Transactional
  public FollowResponse create(FollowRequest request) {
    User follower = userRepository.findById(request.followerId())
        .orElseThrow(EntityNotFoundException::new);
    User followee = userRepository.findById(request.followeeId())
        .orElseThrow(EntityNotFoundException::new);
    Following following = Following.builder()
        .follower(follower)
        .followee(followee)
        .build();
    Following saved = followingRepository.save(following);

    return FollowResponse.from(saved);
  }

}
