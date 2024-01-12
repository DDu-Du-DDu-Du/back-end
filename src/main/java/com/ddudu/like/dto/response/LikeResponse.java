package com.ddudu.like.dto.response;

import com.ddudu.like.domain.Like;
import lombok.Builder;

@Builder
public record LikeResponse(Long id, Long userId, Long todoId, String message) {

  public static LikeResponse from(Like like, String message) {
    return LikeResponse.builder()
        .id(like.getId())
        .userId(like.getUser()
            .getId())
        .todoId(like.getTodo()
            .getId())
        .message(message)
        .build();
  }

}
