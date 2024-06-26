package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyDdudusByGoalUseCase {

  List<GoalGroupedDdudus> get(Long loginId, Long userId, LocalDate date);

}
