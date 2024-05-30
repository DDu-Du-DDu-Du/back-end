package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.response.CompletedDduduNumberStatsResponse;
import com.ddudu.application.port.in.goal.CollectNumberStatsUseCase;
import com.ddudu.application.port.out.goal.NumberStatsPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CollectNumberStatsService implements CollectNumberStatsUseCase {

  private static final int FIRST_DATE = 1;

  private final UserLoaderPort userLoaderPort;
  private final NumberStatsPort numberStatsPort;

  @Override
  public List<CompletedDduduNumberStatsResponse> collectNumberStats(
      Long loginId, YearMonth yearMonth
  ) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, GoalErrorCode.USER_NOT_EXISTING.getCodeName());
    LocalDate from = getFirstDateOfMonth(yearMonth);
    LocalDate to = getLastDateOfMonth(yearMonth);

    return numberStatsPort.collectNumberStats(user, from, to);
  }

  private LocalDate getFirstDateOfMonth(YearMonth yearMonth) {
    if (Objects.nonNull(yearMonth)) {
      return yearMonth.atDay(FIRST_DATE);
    }

    return YearMonth.now()
        .atDay(FIRST_DATE);
  }

  private LocalDate getLastDateOfMonth(YearMonth yearMonth) {
    if (Objects.nonNull(yearMonth)) {
      return yearMonth.atEndOfMonth();
    }

    return YearMonth.now()
        .atEndOfMonth();
  }

}
