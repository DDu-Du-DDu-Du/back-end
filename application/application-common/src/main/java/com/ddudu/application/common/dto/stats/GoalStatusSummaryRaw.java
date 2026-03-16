package com.ddudu.application.common.dto.stats;

import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;

public record GoalStatusSummaryRaw(
    Long dduduId,
    DduduStatus status
) {

}

