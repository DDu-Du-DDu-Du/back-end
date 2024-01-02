package com.ddudu.following.dto.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequest(@NotNull Long followerId, @NotNull Long followeeId) {

}
