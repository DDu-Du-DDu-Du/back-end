package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.response.TimetableResponse;
import java.time.LocalDate;

public interface GetTimetableUseCase {

  TimetableResponse get(Long loginId, Long userId, LocalDate date);

}
