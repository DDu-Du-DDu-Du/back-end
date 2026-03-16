package com.ddudu.application.common.dto.stats.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GoalDetailStatsSummaryResponse(
    Long id,
    String name,
    LocalDateTime createdAt,
    int totalCount,
    int completedCount,
    int completeRate
) {

}
