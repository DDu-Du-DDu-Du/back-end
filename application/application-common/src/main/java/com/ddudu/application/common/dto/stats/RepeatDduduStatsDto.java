package com.ddudu.application.common.dto.stats;

public record RepeatDduduStatsDto(
    Long repeatDduduId,
    String repeatDduduName,
    int completedCount,
    int totalCount
) {

}
