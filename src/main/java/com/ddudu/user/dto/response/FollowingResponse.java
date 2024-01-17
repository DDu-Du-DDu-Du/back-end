package com.ddudu.user.dto.response;

import com.ddudu.user.domain.Following;
import com.ddudu.user.domain.FollowingStatus;
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
