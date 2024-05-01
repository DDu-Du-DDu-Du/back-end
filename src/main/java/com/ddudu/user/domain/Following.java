package com.ddudu.user.domain;

import com.ddudu.common.domain.BaseDomain;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.exception.FollowingErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Following extends BaseDomain {

  private Long id;
  private User follower;
  private User followee;
  private FollowingStatus status;

  @Builder
  public Following(
      Long id, User follower, User followee, FollowingStatus status,
      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isDeleted
  ) {
    super(createdAt, updatedAt, isDeleted);
    validate(follower, followee);

    this.id = id;
    this.follower = follower;
    this.followee = followee;
    this.status = Objects.requireNonNullElse(status, FollowingStatus.FOLLOWING);
  }

  public boolean isRequestedTo(User user) {
    return Objects.equals(followee, user);
  }

  public boolean isOwnedBy(User owner) {
    return Objects.equals(follower, owner);
  }

  public void updateStatus(FollowingStatus status) {
    if (Objects.isNull(status)) {
      throw new InvalidParameterException(FollowingErrorCode.NULL_STATUS_REQUESTED);
    }

    if (!status.isModifiable()) {
      throw new InvalidParameterException(FollowingErrorCode.REQUEST_UNAVAILABLE);
    }

    this.status = status;
  }

  private void validate(User follower, User followee) {
    if (Objects.isNull(follower)) {
      throw new InvalidParameterException(FollowingErrorCode.NULL_FOLLOWER);
    }

    if (Objects.isNull(followee)) {
      throw new InvalidParameterException(FollowingErrorCode.NULL_FOLLOWEE);
    }

    if (follower.equals(followee)) {
      throw new InvalidParameterException(FollowingErrorCode.SELF_FOLLOWING_UNAVAILABLE);
    }
  }

}
