package com.ddudu.application.common.dto.stats;

public record RepeatTodoStatsDto(
    Long repeatTodoId,
    String repeatTodoName,
    int completedCount,
    int totalCount
) {

}
