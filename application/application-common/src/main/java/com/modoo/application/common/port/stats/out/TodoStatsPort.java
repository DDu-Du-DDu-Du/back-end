package com.modoo.application.common.port.stats.out;

import com.modoo.application.common.dto.stats.response.TodoCompletionResponse;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
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
