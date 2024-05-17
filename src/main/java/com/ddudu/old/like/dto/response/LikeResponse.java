package com.ddudu.old.like.dto.response;

import com.ddudu.old.like.domain.Like;
import lombok.Builder;

@Builder
public record LikeResponse(Long id, Long userId, Long todoId, Boolean isDeleted) {

  public static LikeResponse from(Like like) {
    return LikeResponse.builder()
        .id(like.getId())
        .userId(like.getUser()
            .getId())
        .todoId(like.getDdudu()
            .getId())
        .build();
  }

}
