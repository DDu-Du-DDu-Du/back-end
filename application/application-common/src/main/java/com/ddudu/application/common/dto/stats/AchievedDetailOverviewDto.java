package com.ddudu.application.common.dto.stats;

import com.ddudu.common.util.AmPmType;
import lombok.Builder;

@Builder
public record AchievedDetailOverviewDto(
    int achievementCount,
    int totalCount,
    int achievementRate,
    AmPmType mostAchievedTime
) {

}
