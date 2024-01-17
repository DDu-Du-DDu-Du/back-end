package com.ddudu.like.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeRequest(
    @NotNull(message = "좋아요를 누르려는 사용자의 아이디를 확인할 수 없습니다.")
    Long userId,
    @NotNull(message = "좋아요 대상 할 일의 아이디를 확인할 수 없습니다.")
    Long todoId
) {

}
