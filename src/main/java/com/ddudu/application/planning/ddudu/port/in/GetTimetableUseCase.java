package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.response.TimetableResponse;
import java.time.LocalDate;

public interface GetTimetableUseCase {

  TimetableResponse get(Long loginId, Long userId, LocalDate date);

}
