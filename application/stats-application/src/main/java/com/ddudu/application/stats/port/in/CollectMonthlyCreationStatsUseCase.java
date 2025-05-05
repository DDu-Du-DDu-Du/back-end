package com.ddudu.application.stats.port.in;

import com.ddudu.application.stats.dto.CompletionPerGoalDto;
import com.ddudu.application.stats.dto.response.MonthlyStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlyCreationStatsUseCase {

  MonthlyStatsResponse<CompletionPerGoalDto> collectCreation(Long loginId, YearMonth yearMonth);

}
