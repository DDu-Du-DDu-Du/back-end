package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.PostponedPerGoal;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlyPostponementUseCase {

  GenericStatsResponse<PostponedPerGoal> collectPostponement(Long loginId, YearMonth yearMonth);

}
