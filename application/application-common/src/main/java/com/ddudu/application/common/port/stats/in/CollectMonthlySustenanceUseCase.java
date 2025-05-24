package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.SustenancePerGoal;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlySustenanceUseCase {

  GenericStatsResponse<SustenancePerGoal> collectSustenanceCount(Long loginId, YearMonth yearMonth);

}
