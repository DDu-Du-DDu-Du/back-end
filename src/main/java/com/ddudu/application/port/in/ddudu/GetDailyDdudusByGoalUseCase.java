package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.domain.ddudu.dto.response.GoalGroupedDdudusResponse;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyDdudusByGoalUseCase {

  List<GoalGroupedDdudusResponse> get(Long loginId, Long userId, LocalDate date);

}
