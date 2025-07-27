package com.ddudu.application.stats.service;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.stats.AchievementPerGoal;
import com.ddudu.application.common.dto.stats.CreationCountPerGoal;
import com.ddudu.application.common.dto.stats.PostponedPerGoal;
import com.ddudu.application.common.dto.stats.ReattainmentPerGoal;
import com.ddudu.application.common.dto.stats.SustenancePerGoal;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.stats.in.CollectMonthlyStatsSummaryUseCase;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
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

  private MonthlyStatsSummaryResponse collectSummaryByGoal(Map<Long, MonthlyStats> monthlyStats) {
    List<CreationCountPerGoal> creationStats = collectCreation(monthlyStats);
    List<AchievementPerGoal> achievementStats = collectAchievement(monthlyStats);
    List<SustenancePerGoal> sustenanceStats = collectSustenance(monthlyStats);
    List<PostponedPerGoal> postponementStats = collectPostponement(monthlyStats);
    List<ReattainmentPerGoal> reattainmentStats = collectReattainment(monthlyStats);

    return MonthlyStatsSummaryResponse.builder()
        .creationCounts(creationStats)
        .achievements(achievementStats)
        .sustenances(sustenanceStats)
        .postponements(postponementStats)
        .reattainments(reattainmentStats)
        .build();
  }

  private List<CreationCountPerGoal> collectCreation(Map<Long, MonthlyStats> monthlyStats) {
    return monthlyStats.values()
        .stream()
        .map(CreationCountPerGoal::from)
        .sorted((first, second) -> second.count() - first.count())
        .toList();
  }

  private List<AchievementPerGoal> collectAchievement(Map<Long, MonthlyStats> monthlyStats) {
    return monthlyStats.values()
        .stream()
        .map(AchievementPerGoal::from)
        .sorted(Comparator.comparingInt(AchievementPerGoal::achievementRate)
            .reversed())
        .toList();
  }

  private List<SustenancePerGoal> collectSustenance(Map<Long, MonthlyStats> monthlyStats) {
    return monthlyStats.values()
        .stream()
        .map(SustenancePerGoal::from)
        .sorted(Comparator.comparingInt(SustenancePerGoal::sustenanceCount)
            .reversed())
        .toList();
  }

  private List<PostponedPerGoal> collectPostponement(Map<Long, MonthlyStats> monthlyStats) {
    return monthlyStats.values()
        .stream()
        .map(PostponedPerGoal::from)
        .sorted(Comparator.comparingInt(PostponedPerGoal::postponementCount)
            .reversed())
        .toList();
  }

  private List<ReattainmentPerGoal> collectReattainment(Map<Long, MonthlyStats> monthlyStats) {
    return monthlyStats.values()
        .stream()
        .map(ReattainmentPerGoal::from)
        .sorted(Comparator.comparingInt(ReattainmentPerGoal::reattainmentRate)
            .reversed())
        .toList();
  }

}
