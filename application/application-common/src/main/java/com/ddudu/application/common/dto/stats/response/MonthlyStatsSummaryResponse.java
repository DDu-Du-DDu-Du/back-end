package com.ddudu.application.common.dto.stats.response;

import com.ddudu.application.common.dto.stats.AchievementPerGoal;
import com.ddudu.application.common.dto.stats.CreationCountPerGoal;
import com.ddudu.application.common.dto.stats.PostponedPerGoal;
import com.ddudu.application.common.dto.stats.ReattainmentPerGoal;
import com.ddudu.application.common.dto.stats.SustenancePerGoal;
import java.util.List;
import lombok.Builder;

@Builder
public record MonthlyStatsSummaryResponse(
    List<CreationCountPerGoal> creationCounts,
    List<AchievementPerGoal> achievements,
    List<PostponedPerGoal> postponements,
    List<SustenancePerGoal> sustenances,
    List<ReattainmentPerGoal> reattainments
) {

}
