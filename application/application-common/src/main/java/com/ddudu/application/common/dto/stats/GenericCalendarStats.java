package com.ddudu.application.common.dto.stats;

import java.util.List;

public record GenericCalendarStats<T>(
    boolean isAvailable,
    List<T> stats
) {

  public static <T> GenericCalendarStats<T> from(
      boolean isAvailable,
      List<T> stats
  ) {
    return new GenericCalendarStats<>(isAvailable, stats);
  }

}
