package com.ddudu.following.dto.request;

import com.ddudu.following.domain.FollowingStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateFollowingRequest(
    @NotNull(message = "요청할 팔로잉 상태는 필수값입니다.")
    FollowingStatus status
) {

}
