package com.ddudu.application.stats.service;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.stats.PostponedPerGoal;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import com.ddudu.application.common.port.stats.in.CollectMonthlyPostponementUseCase;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectMonthlyPostponementService implements CollectMonthlyPostponementUseCase {

  private final static int FIRST_DATE = 1;

  private final UserLoaderPort userLoaderPort;
  private final MonthlyStatsPort monthlyStatsPort;

  @Override
  public GenericStatsResponse<PostponedPerGoal> collectPostponement(
      Long loginId,
      YearMonth yearMonth
  ) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        StatsErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    if (Objects.isNull(yearMonth)) {
      yearMonth = YearMonth.now();
    }

    LocalDate from = yearMonth.atDay(FIRST_DATE);
    LocalDate to = yearMonth.atEndOfMonth();
    List<PostponedPerGoal> postponementCounts = monthlyStatsPort.collectMonthlyStats(
            user.getId(),
            null,
            from,
            to
        )
        .getOrDefault(yearMonth, MonthlyStats.empty(user.getId(), yearMonth))
        .groupByGoal()
        .values()
        .stream()
        .map(PostponedPerGoal::from)
        .sorted(Comparator.comparingInt(PostponedPerGoal::postponementCount)
            .reversed())
        .toList();

    return GenericStatsResponse.from(postponementCounts);
  }

}
