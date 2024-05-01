package com.ddudu.application.like.dto.response;

import com.ddudu.application.like.domain.Like;
import lombok.Builder;

@Builder
public record LikeResponse(Long id, Long userId, Long todoId, Boolean isDeleted) {

  public static LikeResponse from(Like like) {
    return LikeResponse.builder()
        .id(like.getId())
        .userId(like.getUser()
            .getId())
        .todoId(like.getTodo()
            .getId())
        .isDeleted(like.isDeleted())
        .build();
  }

}
