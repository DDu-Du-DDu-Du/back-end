package com.ddudu.application.stats.port.out;

import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.stats.dto.response.DduduCompletionResponse;
import java.time.LocalDate;
import java.util.List;

public interface DduduStatsPort {

  List<DduduCompletionResponse> calculateDdudusCompletion(
      LocalDate startDate, LocalDate endDate, User user, List<PrivacyType> privacyTypes
  );

}
