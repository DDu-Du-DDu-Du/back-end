package com.ddudu.following.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.following.domain.Following;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.response.FollowResponse;
import com.ddudu.following.exception.FollowingErrorCode;
import com.ddudu.following.repository.FollowingRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowingService {

  private final UserRepository userRepository;
  private final FollowingRepository followingRepository;

  @Transactional
  @Validated
  public FollowResponse create(Long followerId, @Valid FollowRequest request) {
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new DataNotFoundException(FollowingErrorCode.FOLLOWER_NOT_EXISTING));
    User followee = userRepository.findById(request.followeeId())
        .orElseThrow(() -> new DataNotFoundException(FollowingErrorCode.FOLLOWEE_NOT_EXISTING));

    if (followingRepository.existsByFollowerAndFollowee(follower, followee)) {
      throw new DuplicateResourceException(FollowingErrorCode.ALREADY_FOLLOWING);
    }

    Following following = Following.builder()
        .follower(follower)
        .followee(followee)
        .build();
    Following saved = followingRepository.save(following);

    return FollowResponse.from(saved);
  }

}
