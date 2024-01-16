package com.ddudu.user.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ErrorCode;
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
    User follower = findUser(followerId, FollowingErrorCode.FOLLOWER_NOT_EXISTING);
    User followee = findUser(request.followeeId(), FollowingErrorCode.FOLLOWEE_NOT_EXISTING);

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
    Following following = findFollowing(followingId, FollowingErrorCode.ID_NOT_EXISTING);

    checkPermission(followerId, following);
    following.updateStatus(request.status());

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

  private User findUser(Long id, ErrorCode errorCode) {
    return userRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private Following findFollowing(Long id, ErrorCode errorCode) {
    return followingRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private void checkPermission(Long followerId, Following following) {
    if (!following.isOwnedBy(followerId)) {
      throw new ForbiddenException(FollowingErrorCode.WRONG_OWNER);
    }
  }

}
