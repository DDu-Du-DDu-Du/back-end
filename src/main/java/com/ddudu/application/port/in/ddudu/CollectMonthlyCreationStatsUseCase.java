package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.stats.CompletionPerGoalDto;
import com.ddudu.application.dto.stats.response.MonthlyStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlyCreationStatsUseCase {

  MonthlyStatsResponse<CompletionPerGoalDto> collectCreation(Long loginId, YearMonth yearMonth);

}
