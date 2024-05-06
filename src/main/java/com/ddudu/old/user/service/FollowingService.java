package com.ddudu.old.user.service;

import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.DuplicateResourceException;
import com.ddudu.presentation.api.exception.ErrorCode;
import com.ddudu.presentation.api.exception.ForbiddenException;
import com.ddudu.old.user.domain.Following;
import com.ddudu.old.user.domain.Following.FollowingBuilder;
import com.ddudu.old.user.domain.FollowingRepository;
import com.ddudu.old.user.domain.FollowingStatus;
import com.ddudu.old.user.domain.Options;
import com.ddudu.old.user.domain.User;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.old.user.dto.request.FollowRequest;
import com.ddudu.old.user.dto.request.UpdateFollowingRequest;
import com.ddudu.old.user.dto.response.FollowingResponse;
import com.ddudu.old.user.exception.FollowingErrorCode;
import com.ddudu.old.user.exception.UserErrorCode;
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
    User user = findUser(followerId, UserErrorCode.ID_NOT_EXISTING);
    Following following = findFollowing(followingId, FollowingErrorCode.ID_NOT_EXISTING);

    if (!following.isRequestedTo(user)) {
      throw new ForbiddenException(FollowingErrorCode.NOT_ENGAGED_USER);
    }

    following.updateStatus(request.status());

    followingRepository.update(following);

    return FollowingResponse.from(following);
  }

  @Transactional
  public void delete(Long followerId, Long followingId) {
    User owner = findUser(followerId, UserErrorCode.ID_NOT_EXISTING);

    followingRepository.findById(followingId)
        .ifPresent(following -> {
          checkPermission(owner, following);
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

  private void checkPermission(User owner, Following following) {
    if (!following.isOwnedBy(owner)) {
      throw new ForbiddenException(FollowingErrorCode.WRONG_OWNER);
    }
  }

}
