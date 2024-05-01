package com.ddudu.application.todo.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TodoCompletionResponse(
    LocalDate date,
    int totalCount,
    int uncompletedCount
) {

  public static TodoCompletionResponse createEmptyResponse(LocalDate date) {
    return TodoCompletionResponse.builder()
        .date(date)
        .totalCount(0)
        .uncompletedCount(0)
        .build();
  }

}
