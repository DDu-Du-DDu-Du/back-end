package com.modoo.application.common.dto.stats;

import com.modoo.common.util.AmPmType;
import lombok.Builder;

@Builder
public record AchievedDetailOverviewDto(
    int achievementCount,
    int totalCount,
    int achievementRate,
    AmPmType mostAchievedTime
) {

}
