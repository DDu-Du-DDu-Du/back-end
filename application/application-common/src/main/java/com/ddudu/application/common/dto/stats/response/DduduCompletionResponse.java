package com.ddudu.application.common.dto.stats.response;

import java.time.LocalDate;

public record DduduCompletionResponse(
    LocalDate date,
    int totalCount,
    int completedCount,
    int uncompletedCount
) {

}
