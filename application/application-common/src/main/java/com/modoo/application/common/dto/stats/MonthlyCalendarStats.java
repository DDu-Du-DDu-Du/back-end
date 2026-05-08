package com.modoo.application.common.dto.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.YearMonth;
import java.util.List;

public record MonthlyCalendarStats<T>(
    @Schema(
        type = "string",
        pattern = "yyyy-MM",
        example = "2026-05"
    )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM"
    )
    YearMonth yearMonth,
    List<T> stats
) {

  public static <T> MonthlyCalendarStats<T> from(YearMonth yearMonth, List<T> stats) {
    return new MonthlyCalendarStats<>(yearMonth, stats);
  }

}
