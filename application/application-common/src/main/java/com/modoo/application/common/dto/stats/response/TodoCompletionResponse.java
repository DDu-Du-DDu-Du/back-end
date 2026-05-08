package com.modoo.application.common.dto.stats.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoCompletionResponse(
    LocalDate date,
    int totalCount,
    int completedCount,
    int uncompletedCount
) {

  public TodoCompletionResponse(
      LocalDateTime dateTime,
      int totalCount,
      int completedCount,
      int uncompletedCount
  ) {
    this(dateTime.toLocalDate(), totalCount, completedCount, uncompletedCount);
  }

}
