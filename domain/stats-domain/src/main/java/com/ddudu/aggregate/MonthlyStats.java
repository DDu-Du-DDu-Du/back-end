package com.ddudu.aggregate;

import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.common.util.AmPmType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MonthlyStats {

  private final Long userId;
  private final YearMonth yearMonth;
  private final List<BaseStats> stats;

  @Builder
  private MonthlyStats(Long userId, YearMonth yearMonth, List<BaseStats> stats) {
    this.userId = userId;
    this.yearMonth = yearMonth;
    this.stats = Objects.requireNonNullElseGet(stats, Collections::emptyList);
  }

  public static MonthlyStats empty(Long userId, YearMonth yearMonth) {
    return MonthlyStats.builder()
        .userId(userId)
        .yearMonth(yearMonth)
        .build();
  }

  public Map<Long, MonthlyStats> groupByGoal() {
    return stats.stream()
        .collect(
            Collectors.groupingBy(
                BaseStats::getGoalId,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    goalStats -> MonthlyStats.builder()
                        .userId(userId)
                        .yearMonth(yearMonth)
                        .stats(goalStats)
                        .build()
                )
            )
        );
  }

  public Long getGoalId() {
    validateStatsUnderSameGoal();

    return stats.get(0)
        .getGoalId();
  }

  public String getGoalName() {
    validateStatsUnderSameGoal();

    return stats.get(0)
        .getGoalName();
  }

  public int size() {
    return stats.size();
  }

  public int countAchievements() {
    return (int) stats.stream()
        .filter(BaseStats::isCompleted)
        .count();
  }

  public int calculateAchievementRate() {
    int achieved = countAchievements();

    return Math.round((float) achieved / stats.size() * 100);
  }

  public int calculateSustenanceCount() {
    int max = 0;
    int count = 0;
    LocalDate lastSchedule = null;

    for (BaseStats stat : stats) {
      if (!Objects.equals(lastSchedule, stat.getScheduledOn())) {
        count = stat.isCompleted() ? count + 1 : 1;
        lastSchedule = stat.getScheduledOn();
        max = Math.max(max, count);
      }
    }

    return max;
  }

  public int calculatePostponementCount() {
    return (int) stats.stream()
        .filter(BaseStats::isPostponed)
        .count();
  }

  public int calculatePostponementRate() {
    int postponed = calculatePostponementCount();

    return Math.round((float) postponed / stats.size() * 100);
  }

  public int calculateReattainmentCount() {
    List<BaseStats> postponed = stats.stream()
        .filter(BaseStats::isPostponed)
        .toList();

    return (int) postponed.stream()
        .filter(BaseStats::isCompleted)
        .count();
  }

  public int calculateReattainmentRate() {
    List<BaseStats> postponed = stats.stream()
        .filter(BaseStats::isPostponed)
        .toList();

    long reattained = postponed.stream()
        .filter(BaseStats::isCompleted)
        .count();

    return Math.round((float) reattained / stats.size() * 100);
  }

  public MonthlyStats merge(MonthlyStats monthlyStats) {
    List<BaseStats> mergedStats = Stream.concat(stats.stream(), monthlyStats.stats.stream())
        .toList();

    return MonthlyStats.builder()
        .userId(userId)
        .yearMonth(yearMonth)
        .stats(mergedStats)
        .build();
  }

  public AmPmType getMostActiveTime() {
    if (stats.isEmpty()) {
      return AmPmType.NONE;
    }

    long mostActive = stats.stream()
        .map(BaseStats::getTimePart)
        .reduce(0L, Long::sum);

    if (mostActive == 0L) {
      return AmPmType.BOTH;
    }

    return mostActive < 0L ? AmPmType.AM : AmPmType.PM;
  }

  public Map<DayOfWeek, Integer> collectDayOfWeek() {
    Map<DayOfWeek, Integer> collected = new EnumMap<>(DayOfWeek.class);

    for (DayOfWeek day : DayOfWeek.values()) {
      collected.putIfAbsent(day, 0);
    }

    stats.stream()
        .filter(BaseStats::isCompleted)
        .forEach(stat -> collected.merge(stat.getDayOfWeek(), 1, Integer::sum));

    return collected;
  }

  private void validateStatsUnderSameGoal() {
    if (stats.isEmpty()) {
      throw new RuntimeException(StatsErrorCode.MONTHLY_STATS_EMPTY.getCodeName());
    }

    Long goalId = stats.get(0)
        .getGoalId();

    boolean allStatsUnderSameGoal = stats.stream()
        .allMatch(baseStats -> baseStats.isUnderSameGoal(goalId));

    if (!allStatsUnderSameGoal) {
      throw new RuntimeException(StatsErrorCode.MONTHLY_STATS_NOT_GROUPED_BY_GOAL.getCodeName());
    }
  }

}
