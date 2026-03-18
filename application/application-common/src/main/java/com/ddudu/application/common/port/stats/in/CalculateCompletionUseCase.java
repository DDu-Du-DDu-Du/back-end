package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.response.TodoCompletionResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface CalculateCompletionUseCase {

  @Deprecated
  List<TodoCompletionResponse> calculateWeekly(Long loginId, Long userId, LocalDate date);

  List<TodoCompletionResponse> calculateMonthly(Long loginId, Long userId, YearMonth yearMonth);

}
