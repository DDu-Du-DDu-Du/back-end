package com.ddudu.application.todo.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record LikeInfo(
    int count,
    List<Long> users
) {

  public static LikeInfo from(List<Long> users) {
    return LikeInfo.builder()
        .count(users.size())
        .users(users)
        .build();
  }

}
