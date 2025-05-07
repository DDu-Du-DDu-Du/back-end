package com.ddudu.application.port.stats.in;

import com.ddudu.application.dto.stats.CompletionPerGoalDto;
import com.ddudu.application.dto.stats.response.GenericStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlyCreationStatsUseCase {

  GenericStatsResponse<CompletionPerGoalDto> collectCreation(Long loginId, YearMonth yearMonth);

}
