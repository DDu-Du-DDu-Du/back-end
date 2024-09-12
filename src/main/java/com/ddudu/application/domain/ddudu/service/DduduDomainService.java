package com.ddudu.application.domain.ddudu.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.dto.stats.MonthlyStatsSummaryDto;
import com.ddudu.application.dto.stats.StatsBaseDto;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DduduDomainService {

  public Ddudu create(User user, CreateDduduRequest request) {
    return Ddudu.builder()
        .userId(user.getId())
        .goalId(request.goalId())
        .name(request.name())
        .scheduledOn(request.scheduledOn())
        .build();
  }

  public MonthlyStatsSummaryDto calculateMonthlyStats(
      YearMonth yearMonth, List<StatsBaseDto> stats
  ) {
    return MonthlyStatsSummaryDto.of(
        yearMonth, stats.size(), calculateAchievementPercentage(stats),
        calculateSustenanceCount(stats), calculatePostponementCount(stats),
        calculateReattainmentCount(stats)
    );
  }

  public int calculateAchievementPercentage(List<StatsBaseDto> stats) {
    long achieved = stats.stream()
        .filter(stat -> stat.status()
            .isCompleted())
        .count();

    return Math.round((float) achieved / stats.size() * 100);
  }

  public int calculateSustenanceCount(List<StatsBaseDto> stats) {
    int max = 0;
    int count = 0;
    LocalDate lastSchedule = null;

    for (StatsBaseDto stat : stats) {
      if (!Objects.equals(lastSchedule, stat.scheduledOn())) {
        count = stat.status()
            .isCompleted() ? count + 1 : 1;
        lastSchedule = stat.scheduledOn();
        max = Math.max(max, count);
      }
    }

    return max;
  }

  public int calculatePostponementCount(List<StatsBaseDto> stats) {
    return (int) stats.stream()
        .filter(StatsBaseDto::isPostponed)
        .count();
  }

  public int calculateReattainmentCount(List<StatsBaseDto> stats) {
    List<StatsBaseDto> postponed = stats.stream()
        .filter(StatsBaseDto::isPostponed)
        .toList();

    long reattained = postponed.stream()
        .filter(stat -> stat.status()
            .isCompleted())
        .count();

    return Math.round((float) reattained / stats.size() * 100);
  }

}
