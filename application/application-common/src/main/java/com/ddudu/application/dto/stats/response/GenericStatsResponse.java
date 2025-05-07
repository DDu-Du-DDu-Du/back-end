package com.ddudu.application.dto.stats.response;

import java.util.List;

public record GenericStatsResponse<T>(
    boolean isEmpty,
    List<T> contents
) {

  public static <T> GenericStatsResponse<T> from(List<T> stats) {
    return new GenericStatsResponse<>(stats.isEmpty(), stats);
  }

}
