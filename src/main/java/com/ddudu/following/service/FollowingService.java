package com.ddudu.following.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.InvalidAuthenticationException;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.following.domain.Following;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.request.UpdateFollowingRequest;
import com.ddudu.following.dto.response.FollowingResponse;
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
  public FollowingResponse create(Long followerId, @Valid FollowRequest request) {
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

    return FollowingResponse.from(saved);
  }

  @Transactional
  public FollowingResponse updateStatus(Long id, Long followerId, UpdateFollowingRequest request) {
    Following following = followingRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(FollowingErrorCode.ID_NOT_EXISTING));

    if (!following.isOwnedBy(followerId)) {
      throw new InvalidAuthenticationException(FollowingErrorCode.WRONG_OWNER);
    }

    if (!request.status()
        .isModifiable()) {
      throw new InvalidParameterException(FollowingErrorCode.REQUEST_UNAVAILABLE);
    }

    following.updateStatus(request.status());

    return FollowingResponse.from(following);
  }

}
