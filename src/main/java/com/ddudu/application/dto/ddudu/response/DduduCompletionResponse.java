package com.ddudu.application.dto.ddudu.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DduduCompletionResponse(
    LocalDate date,
    int totalCount,
    int uncompletedCount
) {

  public static DduduCompletionResponse createEmptyResponse(LocalDate date) {
    return DduduCompletionResponse.builder()
        .date(date)
        .totalCount(0)
        .uncompletedCount(0)
        .build();
  }

}
