package com.ddudu.application.common.dto.stats.response;

import com.ddudu.application.common.dto.stats.AchievedDetailOverviewDto;
import com.ddudu.application.common.dto.stats.DayOfWeekStatsDto;
import com.ddudu.application.common.dto.stats.GenericCalendarStats;
import com.ddudu.application.common.dto.stats.RepeatTodoStatsDto;
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
