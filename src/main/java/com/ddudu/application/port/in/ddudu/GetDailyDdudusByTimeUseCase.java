package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.domain.ddudu.dto.response.TimeGroupedDdudus;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyDdudusByTimeUseCase {

  List<TimeGroupedDdudus> get(Long loginId, Long userId, LocalDate date);

}
