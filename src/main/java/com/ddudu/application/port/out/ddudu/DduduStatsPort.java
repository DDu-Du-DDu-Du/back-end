package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface DduduStatsPort {

  List<DduduCompletionResponse> calculateDdudusCompletion(
      LocalDateTime startDate, LocalDateTime endDate, User user, List<PrivacyType> privacyTypes
  );

}
