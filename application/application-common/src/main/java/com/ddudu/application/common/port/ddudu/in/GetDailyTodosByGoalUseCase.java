package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.GoalGroupedTodos;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyTodosByGoalUseCase {

  List<GoalGroupedTodos> get(Long loginId, Long userId, LocalDate date);

}
