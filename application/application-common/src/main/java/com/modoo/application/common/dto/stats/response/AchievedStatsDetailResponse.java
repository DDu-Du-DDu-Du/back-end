package com.modoo.application.common.dto.stats.response;

import com.modoo.application.common.dto.stats.AchievedDetailOverviewDto;
import com.modoo.application.common.dto.stats.DayOfWeekStatsDto;
import com.modoo.application.common.dto.stats.GenericCalendarStats;
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
    GenericCalendarStats<TodoCompletionResponse> calendarStats
) {

}
