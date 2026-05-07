package com.modoo.application.common.dto.stats.response;

import com.modoo.application.common.dto.stats.AchievedDetailOverviewDto;
import com.modoo.application.common.dto.stats.DayOfWeekStatsDto;
import com.modoo.application.common.dto.stats.MonthlyCalendarStats;
import com.modoo.application.common.dto.stats.RepeatTodoStatsDto;
import java.util.List;
import lombok.Builder;

@Builder
public record AchievedStatsDetailResponse(
    Long goalId,
    String goalColor,
    AchievedDetailOverviewDto overview,
    DayOfWeekStatsDto dayOfWeekStats,
    List<RepeatTodoStatsDto> repeatTodoStats,
    List<MonthlyCalendarStats<TodoCompletionResponse>> calendarStats
) {

}
