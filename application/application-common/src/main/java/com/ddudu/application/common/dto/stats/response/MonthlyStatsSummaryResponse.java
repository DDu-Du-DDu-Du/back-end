package com.ddudu.application.common.dto.stats.response;

import com.ddudu.application.common.dto.stats.GoalMonthlyStatsSummary;
import java.util.List;
import lombok.Builder;

@Builder
public record MonthlyStatsSummaryResponse(
    List<GoalMonthlyStatsSummary> summaries
) {

}
