package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.response.TimetableResponse;
import java.time.LocalDate;

public interface GetTimetableUseCase {

  TimetableResponse get(Long loginId, Long userId, LocalDate date);

}
