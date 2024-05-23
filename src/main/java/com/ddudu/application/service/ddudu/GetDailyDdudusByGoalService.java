package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.dto.response.GoalGroupedDdudusResponse;
import com.ddudu.application.port.in.ddudu.GetDailyDdudusByGoalUseCase;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDailyDdudusByGoalService implements GetDailyDdudusByGoalUseCase {

  @Override
  public List<GoalGroupedDdudusResponse> get(Long loginId, Long userId, LocalDate date) {
    return null;
  }

}
