package com.ddudu.application.stats.service;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.stats.GoalMonthlyStatsSummary;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.stats.in.CollectMonthlyStatsSummaryUseCase;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectMonthlyStatsSummaryService implements CollectMonthlyStatsSummaryUseCase {

  private static final int FIRST_DATE = 1;

  private final UserLoaderPort userLoaderPort;
  private final MonthlyStatsPort monthlyStatsPort;

  @Override
  public MonthlyStatsSummaryResponse collectSummary(
      Long loginId,
      Long userId,
      YearMonth yearMonth
  ) {
    Long targetUserId = Objects.nonNull(userId) ? userId : loginId;

    User user = userLoaderPort.getUserOrElseThrow(
        targetUserId,
        StatsErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    if (Objects.isNull(yearMonth)) {
      yearMonth = YearMonth.now();
    }

    LocalDate from = yearMonth.atDay(FIRST_DATE);
    LocalDate to = yearMonth.atEndOfMonth();

    Map<Long, MonthlyStats> monthlyStatsByGoal = monthlyStatsPort.collectMonthlyStats(
            user.getId(),
            null,
            from,
            to
        )
        .getOrDefault(yearMonth, MonthlyStats.empty(user.getId(), yearMonth))
        .groupByGoal();

    return collectSummaryByGoal(monthlyStatsByGoal);
  }

  private MonthlyStatsSummaryResponse collectSummaryByGoal(
      Map<Long, MonthlyStats> monthlyStatsByGoal
  ) {
    List<GoalMonthlyStatsSummary> summaries = monthlyStatsByGoal.values()
        .stream()
        .map(this::toSummary)
        .sorted(this::compareByCreationCountDescThenGoalIdAsc)
        .toList();

    return MonthlyStatsSummaryResponse.builder()
        .summaries(summaries)
        .build();
  }

  private int compareByCreationCountDescThenGoalIdAsc(
      GoalMonthlyStatsSummary first,
      GoalMonthlyStatsSummary second
  ) {
    int compare = Integer.compare(second.creationCount(), first.creationCount());

    if (compare != 0) {
      return compare;
    }

    return Long.compare(first.goalId(), second.goalId());
  }

  private GoalMonthlyStatsSummary toSummary(MonthlyStats monthlyStats) {
    return GoalMonthlyStatsSummary.builder()
        .goalId(monthlyStats.getGoalId())
        .goalName(monthlyStats.getGoalName())
        .goalColor(monthlyStats.getGoalColor())
        .creationCount(monthlyStats.size())
        .achievementCount(monthlyStats.countAchievements())
        .postponedCount(monthlyStats.calculatePostponementCount())
        .sustainedCount(monthlyStats.calculateSustenanceCount())
        .reattainedCount(monthlyStats.calculateReattainmentCount())
        .build();
  }

}
