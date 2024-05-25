package com.ddudu.application.domain.ddudu.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import lombok.Builder;

@Builder
public record DduduWithColorInfo(
    Long id,
    String name,
    DduduStatus status,
    String color
) {

  public static DduduWithColorInfo of(Ddudu ddudu, String color) {
    return DduduWithColorInfo.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .color(color)
        .build();
  }

}
