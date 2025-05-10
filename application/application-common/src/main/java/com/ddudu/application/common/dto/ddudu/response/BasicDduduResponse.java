package com.ddudu.application.common.dto.ddudu.response;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import lombok.Builder;

@Builder
public record BasicDduduResponse(
    Long id,
    String name,
    DduduStatus status
) {

  public static BasicDduduResponse from(Ddudu ddudu) {
    return BasicDduduResponse.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .build();
  }

}
