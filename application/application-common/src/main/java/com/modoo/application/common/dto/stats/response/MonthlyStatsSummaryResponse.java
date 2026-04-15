package com.modoo.application.common.dto.stats.response;

import com.modoo.application.common.dto.stats.GoalMonthlyStatsSummary;
import java.util.List;
import lombok.Builder;

@Builder
public record MonthlyStatsSummaryResponse(
    List<GoalMonthlyStatsSummary> summaries
) {

}
