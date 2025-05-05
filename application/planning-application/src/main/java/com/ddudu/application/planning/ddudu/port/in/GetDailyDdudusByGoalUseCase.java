package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.GoalGroupedDdudus;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyDdudusByGoalUseCase {

  List<GoalGroupedDdudus> get(Long loginId, Long userId, LocalDate date);

}
