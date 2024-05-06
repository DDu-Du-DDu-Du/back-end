package com.ddudu.old.user.dto.response;

import com.ddudu.old.user.domain.Following;
import com.ddudu.old.user.domain.FollowingStatus;
import lombok.Builder;

@Builder
public record FollowingResponse(Long id, Long followerId, Long followeeId, FollowingStatus status) {

  public static FollowingResponse from(Following following) {
    return FollowingResponse.builder()
        .id(following.getId())
        .followerId(following.getFollower()
            .getId())
        .followeeId(following.getFollowee()
            .getId())
        .status(following.getStatus())
        .build();
  }

}
