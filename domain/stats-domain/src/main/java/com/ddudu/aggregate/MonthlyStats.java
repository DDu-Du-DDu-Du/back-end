package com.ddudu.aggregate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class MonthlyStats {

  private final Long userId;
  private final YearMonth yearMonth;
  private final List<BaseStats> stats;

  public Map<Long, Integer> countPerGoal() {
    return stats.stream()
        .collect(
            Collectors.groupingBy(
                BaseStats::getGoalId,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    List::size
                )
            )
        );
  }

  public int size() {
    return stats.size();
  }

  public int calculateAchievementPercentage() {
    long achieved = stats.stream()
        .filter(BaseStats::isCompleted)
        .count();

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

  public int calculateReattainmentCount() {
    List<BaseStats> postponed = stats.stream()
        .filter(BaseStats::isPostponed)
        .toList();

    long reattained = postponed.stream()
        .filter(BaseStats::isCompleted)
        .count();

    return Math.round((float) reattained / stats.size() * 100);
  }

}
