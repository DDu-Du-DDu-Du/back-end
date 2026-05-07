package com.modoo.common.time;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DateTimeRange(
    LocalDateTime start,
    LocalDateTime end
) {

  public LocalDate startDate() {
    return start.toLocalDate();
  }

  public LocalDate endDate() {
    return end.toLocalDate();
  }

}
