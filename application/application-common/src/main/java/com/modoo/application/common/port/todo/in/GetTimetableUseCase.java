package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.response.TimetableResponse;
import java.time.LocalDate;

public interface GetTimetableUseCase {

  TimetableResponse get(Long loginId, Long userId, LocalDate date, String timeZone);

}
