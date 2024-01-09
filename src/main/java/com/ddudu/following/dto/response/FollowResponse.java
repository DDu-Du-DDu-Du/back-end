package com.ddudu.following.dto.response;

import com.ddudu.following.domain.Following;
import com.ddudu.following.domain.FollowingStatus;
import lombok.Builder;

@Builder
public record FollowResponse(Long id, Long followerId, Long followeeId, FollowingStatus status) {

  public static FollowResponse from(Following following) {
    return FollowResponse.builder()
        .id(following.getId())
        .followerId(following.getFollower()
            .getId())
        .followeeId(following.getFollowee()
            .getId())
        .build();
  }

}
