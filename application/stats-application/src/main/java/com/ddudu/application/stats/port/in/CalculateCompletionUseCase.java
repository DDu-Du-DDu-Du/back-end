package com.ddudu.application.stats.port.in;

import com.ddudu.application.stats.dto.response.DduduCompletionResponse;
import java.time.LocalDate;
import java.util.List;

public interface CalculateCompletionUseCase {

  List<DduduCompletionResponse> calculate(Long loginId, Long userId, LocalDate from, LocalDate to);

}
