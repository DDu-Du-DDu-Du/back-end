package com.ddudu.application.common.port.stats.out;

import com.ddudu.application.common.dto.stats.response.TodoCompletionResponse;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import java.time.LocalDate;
import java.util.List;

public interface TodoStatsPort {

  List<TodoCompletionResponse> calculateTodosCompletion(
      LocalDate startDate,
      LocalDate endDate,
      Long userId,
      Long goalId,
      List<PrivacyType> privacyTypes,
      boolean isAchieved
  );

}
