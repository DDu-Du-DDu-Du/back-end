package com.ddudu.application.domain.ddudu.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.vo.Color;
import lombok.Builder;

@Builder
public record DduduWithColorInfo(
    Long id,
    String name,
    DduduStatus status,
    String color
) {

  public static DduduWithColorInfo from(Ddudu ddudu, Color color) {
    return DduduWithColorInfo.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .color(color.getCode())
        .build();
  }

}