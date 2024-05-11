package com.ddudu.old.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequest(
    @NotNull(message = "팔로잉 대상 사용자의 아이디를 확인할 수 없습니다.")
    Long followeeId
) {

}
