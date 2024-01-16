package com.ddudu.user.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.domain.Following;
import com.ddudu.user.domain.Following.FollowingBuilder;
import com.ddudu.user.domain.FollowingStatus;
import com.ddudu.user.domain.Options;
import com.ddudu.user.domain.User;
import com.ddudu.user.dto.request.FollowRequest;
import com.ddudu.user.dto.request.UpdateFollowingRequest;
import com.ddudu.user.dto.response.FollowingResponse;
import com.ddudu.user.exception.FollowingErrorCode;
import com.ddudu.user.repository.FollowingRepository;
import com.ddudu.user.repository.UserRepository;
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
  public FollowingResponse create(Long followerId, FollowRequest request) {
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new DataNotFoundException(FollowingErrorCode.FOLLOWER_NOT_EXISTING));
    User followee = userRepository.findById(request.followeeId())
        .orElseThrow(() -> new DataNotFoundException(FollowingErrorCode.FOLLOWEE_NOT_EXISTING));

    if (followingRepository.existsByFollowerAndFollowee(follower, followee)) {
      throw new DuplicateResourceException(FollowingErrorCode.ALREADY_FOLLOWING);
    }

    FollowingBuilder followingBuilder = Following.builder()
        .follower(follower)
        .followee(followee);

    Options followeeOptions = followee.getOptions();

    if (followeeOptions.isAllowingFollowsAfterApproval()) {
      followingBuilder.status(FollowingStatus.REQUESTED);
    }

    Following saved = followingRepository.save(followingBuilder.build());

    return FollowingResponse.from(saved);
  }

  @Transactional
  public FollowingResponse updateStatus(
      Long followerId, Long followingId, UpdateFollowingRequest request
  ) {
    Following following = followingRepository.findById(followingId)
        .orElseThrow(() -> new DataNotFoundException(FollowingErrorCode.ID_NOT_EXISTING));

    if (!following.isOwnedBy(followerId)) {
      throw new ForbiddenException(FollowingErrorCode.WRONG_OWNER);
    }

    FollowingStatus requestedStatus = request.status();

    if (!requestedStatus.isModifiable()) {
      throw new InvalidParameterException(FollowingErrorCode.REQUEST_UNAVAILABLE);
    }

    following.updateStatus(requestedStatus);

    return FollowingResponse.from(following);
  }

  @Transactional
  public void delete(Long followerId, Long followingId) {
    followingRepository.findById(followingId)
        .ifPresent(following -> {
          checkPermission(followerId, following);
          followingRepository.delete(following);
        });
  }

  private void checkPermission(Long loginId, Following following) {
    if (!following.isOwnedBy(loginId)) {
      throw new ForbiddenException(FollowingErrorCode.WRONG_OWNER);
    }
  }

}
