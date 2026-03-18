package com.ddudu.application.common.dto.stats.response;

import com.ddudu.application.common.dto.stats.DayOfWeekStatsDto;
import com.ddudu.application.common.dto.stats.GenericCalendarStats;
import com.ddudu.application.common.dto.stats.PostponedDetailOverviewDto;
import lombok.Builder;

@Builder
public record PostponedStatsDetailResponse(
    Long goalId,
    String goalColor,
    PostponedDetailOverviewDto overview,
    DayOfWeekStatsDto dayOfWeekStats,
    GenericCalendarStats<TodoCompletionResponse> calendarStats
) {

}
