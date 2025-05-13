package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.CompletionPerGoalDto;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlyCreationStatsUseCase {

  GenericStatsResponse<CompletionPerGoalDto> collectCreation(Long loginId, YearMonth yearMonth);

}
