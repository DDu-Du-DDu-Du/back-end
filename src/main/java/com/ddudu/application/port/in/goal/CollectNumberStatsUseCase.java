package com.ddudu.application.port.in.goal;

import com.ddudu.application.dto.goal.response.CompletedDduduNumberStatsResponse;
import java.time.YearMonth;
import java.util.List;

public interface CollectNumberStatsUseCase {

  List<CompletedDduduNumberStatsResponse> collectNumberStats(
      Long loginId, YearMonth yearMonth
  );

}
