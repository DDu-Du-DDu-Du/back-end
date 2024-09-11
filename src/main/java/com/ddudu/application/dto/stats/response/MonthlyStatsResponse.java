package com.ddudu.application.dto.stats.response;

import java.util.List;

public record MonthlyStatsResponse<T>(
    boolean isEmpty,
    List<T> contents
) {

  public static <T> MonthlyStatsResponse<T> from(List<T> stats) {
    return new MonthlyStatsResponse<>(stats.isEmpty(), stats);
  }

}
