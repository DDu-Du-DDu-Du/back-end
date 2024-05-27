package com.ddudu.application.domain.ddudu.dto;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import lombok.Builder;

@Builder
public record BasicDduduWithColor(
    Long id,
    String name,
    DduduStatus status,
    String color
) {

  public static BasicDduduWithColor of(Ddudu ddudu, String color) {
    return BasicDduduWithColor.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .color(color)
        .build();
  }

}
