package com.ddudu.application.port.ddudu.in;

import com.ddudu.application.dto.ddudu.response.TimetableResponse;
import java.time.LocalDate;

public interface GetTimetableUseCase {

  TimetableResponse get(Long loginId, Long userId, LocalDate date);

}
