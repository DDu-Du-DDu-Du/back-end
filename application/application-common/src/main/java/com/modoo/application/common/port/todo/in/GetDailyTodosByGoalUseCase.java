package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.GoalGroupedTodos;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyTodosByGoalUseCase {

  List<GoalGroupedTodos> get(Long loginId, Long userId, LocalDate date, String timeZone);

}
