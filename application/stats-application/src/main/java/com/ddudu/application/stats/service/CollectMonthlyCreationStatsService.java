package com.ddudu.application.stats.service;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.stats.CompletionPerGoalDto;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import com.ddudu.application.common.port.stats.in.CollectMonthlyCreationStatsUseCase;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectMonthlyCreationStatsService implements CollectMonthlyCreationStatsUseCase {

  private static final int FIRST_DATE = 1;

  private final UserLoaderPort userLoaderPort;
  private final MonthlyStatsPort monthlyStatsPort;

  @Override
  public GenericStatsResponse<CompletionPerGoalDto> collectCreation(
      Long loginId,
      YearMonth yearMonth
  ) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    if (Objects.isNull(yearMonth)) {
      yearMonth = YearMonth.now();
    }

    LocalDate from = yearMonth.atDay(FIRST_DATE);
    LocalDate to = yearMonth.atEndOfMonth();
    MonthlyStats monthlyStats = monthlyStatsPort.collectMonthlyStats(user.getId(), null, from, to)
        .getOrDefault(yearMonth, MonthlyStats.empty(user.getId(), yearMonth));
    List<CompletionPerGoalDto> completions = wrapCompletions(monthlyStats);

    return GenericStatsResponse.from(completions);
  }

  private List<CompletionPerGoalDto> wrapCompletions(MonthlyStats monthlyStats) {
    return monthlyStats.countPerGoal()
        .entrySet()
        .stream()
        .map(countPerGoalEntry ->
            CompletionPerGoalDto.from(
                countPerGoalEntry.getKey(),
                countPerGoalEntry.getValue()
            )
        )
        .sorted((first, second) -> second.count() - first.count())
        .toList();
  }

}
