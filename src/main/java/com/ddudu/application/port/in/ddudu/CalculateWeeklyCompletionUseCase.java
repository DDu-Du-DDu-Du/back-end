package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import java.time.LocalDate;
import java.util.List;

public interface CalculateWeeklyCompletionUseCase {

  List<DduduCompletionResponse> calculate(Long loginId, Long userId, LocalDate date);

}
