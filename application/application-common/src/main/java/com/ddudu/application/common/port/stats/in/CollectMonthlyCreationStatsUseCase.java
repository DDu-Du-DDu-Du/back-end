package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.CreationCountPerGoalDto;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlyCreationStatsUseCase {

  GenericStatsResponse<CreationCountPerGoalDto> collectCreation(Long loginId, YearMonth yearMonth);

}
