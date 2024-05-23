package com.ddudu.application.domain.ddudu.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.old.todo.dto.response.LikeInfo;
import lombok.Builder;

@Builder
public record DduduInfo(
    Long id,
    String name,
    DduduStatus status
) {

  public static DduduInfo from(Ddudu ddudu) {
    return DduduInfo.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .build();
  }

  // TODO: Remove this method after migration
  public static DduduInfo from(Ddudu ddudu, LikeInfo likeInfo) {
    return DduduInfo.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .build();
  }

}
