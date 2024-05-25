package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.domain.ddudu.dto.response.TimetableResponse;
import java.time.LocalDate;

public interface GetTimetableAndUnassignedDdudusUseCase {

  TimetableResponse get(Long loginId, Long userId, LocalDate date);

}
