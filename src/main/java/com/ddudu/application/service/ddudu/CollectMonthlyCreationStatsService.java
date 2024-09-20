package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.stats.CompletionPerGoalDto;
import com.ddudu.application.dto.stats.StatsBaseDto;
import com.ddudu.application.dto.stats.response.MonthlyStatsResponse;
import com.ddudu.application.port.in.ddudu.CollectMonthlyCreationStatsUseCase;
import com.ddudu.application.port.out.goal.MonthlyStatsPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
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
    LocalDate from = yearMonth.atDay(FIRST_DATE);
    LocalDate to = yearMonth.atEndOfMonth();
    List<StatsBaseDto> stats = monthlyStatsPort.collectMonthlyStats(user, null, from, to);
    List<CompletionPerGoalDto> completions = countPerGoalId(stats);

    return MonthlyStatsResponse.from(completions);
  }

  private List<CompletionPerGoalDto> countPerGoalId(List<StatsBaseDto> stats) {
    return stats.stream()
        .collect(Collectors.groupingBy(StatsBaseDto::id, Collectors.summingInt(toAdd -> 1)))
        .entrySet()
        .stream()
        .map(completion -> CompletionPerGoalDto.from(completion.getKey(), completion.getValue()))
        .toList();
  }

}
