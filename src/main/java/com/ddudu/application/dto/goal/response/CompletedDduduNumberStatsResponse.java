package com.ddudu.application.dto.goal.response;

public record CompletedDduduNumberStatsResponse(
    Long goalId,
    String goalName,
    Long completedCount
) {

}
