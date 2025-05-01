package com.ddudu.application.dto.ddudu.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
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
