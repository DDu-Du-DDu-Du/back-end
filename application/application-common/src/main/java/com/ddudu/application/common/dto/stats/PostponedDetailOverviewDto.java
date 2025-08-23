package com.ddudu.application.common.dto.stats;

import lombok.Builder;

@Builder
public record PostponedDetailOverviewDto(
    int postponedCount,
    int reattainedCount,
    int totalCount,
    int postponementRate,
    int reattainmentRate
) {

}
