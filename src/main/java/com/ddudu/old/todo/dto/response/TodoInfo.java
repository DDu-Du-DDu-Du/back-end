package com.ddudu.old.todo.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import lombok.Builder;

@Builder
public record TodoInfo(
    Long id,
    String name,
    DduduStatus status,
    LikeInfo likes
) {

  public static TodoInfo from(Ddudu ddudu) {
    return TodoInfo.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .build();
  }

  public static TodoInfo from(Ddudu ddudu, LikeInfo likeInfo) {
    return TodoInfo.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .likes(likeInfo)
        .build();
  }

}
