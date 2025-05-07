package com.ddudu.application.port.stats.in;

import com.ddudu.application.dto.stats.response.DduduCompletionResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface CalculateCompletionUseCase {

  List<DduduCompletionResponse> calculateWeekly(Long loginId, Long userId, LocalDate date);

  List<DduduCompletionResponse> calculateMonthly(Long loginId, Long userId, YearMonth yearMonth);

}
