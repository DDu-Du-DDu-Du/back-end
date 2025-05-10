package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.GoalGroupedDdudus;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyDdudusByGoalUseCase {

  List<GoalGroupedDdudus> get(Long loginId, Long userId, LocalDate date);

}
