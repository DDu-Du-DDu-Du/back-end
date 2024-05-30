package com.ddudu.application.port.out.goal;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.response.CompletedDduduNumberStatsResponse;
import java.time.LocalDate;
import java.util.List;

public interface NumberStatsPort {

  List<CompletedDduduNumberStatsResponse> collectNumberStats(
      User user, LocalDate from, LocalDate to
  );

}
