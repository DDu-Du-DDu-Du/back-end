package com.ddudu.application.stats.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.stats.dto.CompletionPerGoalDto;
import com.ddudu.application.stats.dto.StatsBaseDto;
import com.ddudu.application.stats.dto.response.MonthlyStatsResponse;
import com.ddudu.application.stats.port.in.CollectMonthlyCreationStatsUseCase;
import com.ddudu.application.planning.goal.port.out.MonthlyStatsPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
  public MonthlyStatsResponse<CompletionPerGoalDto> collectCreation(
      Long loginId, YearMonth yearMonth
  ) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    if (Objects.isNull(yearMonth)) {
      yearMonth = YearMonth.now();
    }

    LocalDate from = yearMonth.atDay(FIRST_DATE);
    LocalDate to = yearMonth.atEndOfMonth();
    List<StatsBaseDto> stats = monthlyStatsPort.collectMonthlyStats(user, null, from, to);
    List<CompletionPerGoalDto> completions = countPerGoalId(stats);

    return MonthlyStatsResponse.from(completions);
  }

  private List<CompletionPerGoalDto> countPerGoalId(List<StatsBaseDto> stats) {
    return stats.stream()
        .collect(Collectors.groupingBy(StatsBaseDto::goalId, Collectors.summingInt(toAdd -> 1)))
        .entrySet()
        .stream()
        .map(completion -> CompletionPerGoalDto.from(completion.getKey(), completion.getValue()))
        .sorted((first, second) -> second.count() - first.count())
        .toList();
  }

}
