package com.modoo.application.common.dto.stats.response;

import com.modoo.application.common.dto.stats.DayOfWeekStatsDto;
import com.modoo.application.common.dto.stats.MonthlyCalendarStats;
import com.modoo.application.common.dto.stats.PostponedDetailOverviewDto;
import java.util.List;
import lombok.Builder;

@Builder
public record PostponedStatsDetailResponse(
    Long goalId,
    String goalColor,
    PostponedDetailOverviewDto overview,
    DayOfWeekStatsDto dayOfWeekStats,
    List<MonthlyCalendarStats<TodoCompletionResponse>> calendarStats
) {

}
