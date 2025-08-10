package com.ddudu.application.common.dto.stats;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record DayOfWeekStatsDto(
    List<DayOfWeek> mostActiveDays,
    Map<DayOfWeek, Integer> stats
) {

}
