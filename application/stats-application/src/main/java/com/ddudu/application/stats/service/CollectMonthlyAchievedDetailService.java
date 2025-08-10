package com.ddudu.application.stats.service;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.stats.AchievedDetailOverviewDto;
import com.ddudu.application.common.dto.stats.DayOfWeekStatsDto;
import com.ddudu.application.common.dto.stats.GenericCalendarStats;
import com.ddudu.application.common.dto.stats.RepeatDduduStatsDto;
import com.ddudu.application.common.dto.stats.response.AchievedStatsDetailResponse;
import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.stats.in.CollectMonthlyAchievedDetailUseCase;
import com.ddudu.application.common.port.stats.out.DduduStatsPort;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectMonthlyAchievedDetailService implements CollectMonthlyAchievedDetailUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final MonthlyStatsPort monthlyStatsPort;
  private final DduduStatsPort dduduStatsPort;

  @Override
  public AchievedStatsDetailResponse collectAchievedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  ) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        StatsErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    fromMonth = Objects.requireNonNullElse(fromMonth, YearMonth.now());
    toMonth = Objects.requireNonNullElse(toMonth, YearMonth.now());

    if (toMonth.isBefore(fromMonth)) {
      throw new IllegalArgumentException(StatsErrorCode.INVALID_TO_MONTH.getCodeName());
    }

    LocalDate from = fromMonth.atDay(1);
    LocalDate to = toMonth.atEndOfMonth();
    Goal goal = validateAndGetGoal(goalId);
    MonthlyStats reduced = collectStatsAtOnce(user.getId(), goal, fromMonth, to);
    AchievedDetailOverviewDto overview = createOverview(reduced);
    DayOfWeekStatsDto dayOfWeekStats = createDayOfWeekStats(reduced);
    List<RepeatDduduStatsDto> repeatDduduCounts = monthlyStatsPort.countRepeatDdudu(
        user.getId(),
        goal.getId(),
        from,
        to
    );
    GenericCalendarStats<DduduCompletionResponse> completions = getCalendarCompletions(
        fromMonth,
        toMonth,
        from,
        to,
        user,
        goal
    );

    return AchievedStatsDetailResponse.builder()
        .overview(overview)
        .dayOfWeekStats(dayOfWeekStats)
        .repeatDduduStats(repeatDduduCounts)
        .goalId(goal.getId())
        .completions(completions)
        .build();
  }

  private Goal validateAndGetGoal(Long goalId) {
    if (Objects.isNull(goalId)) {
      throw new IllegalArgumentException(StatsErrorCode.NULL_GOAL_ID.getCodeName());
    }

    return goalLoaderPort.getGoalOrElseThrow(
        goalId,
        StatsErrorCode.GOAL_NOT_EXISTING.getCodeName()
    );
  }

  private MonthlyStats collectStatsAtOnce(
      Long userId,
      Goal goal,
      YearMonth fromMonth,
      LocalDate to
  ) {
    return monthlyStatsPort.collectMonthlyStats(userId, goal, fromMonth.atDay(1), to)
        .values()
        .stream()
        .reduce(MonthlyStats.empty(userId, fromMonth), MonthlyStats::merge);
  }

  private AchievedDetailOverviewDto createOverview(MonthlyStats monthlyStats) {
    return AchievedDetailOverviewDto.builder()
        .achievementRate(monthlyStats.calculateAchievementRate())
        .achievementCount(monthlyStats.countAchievements())
        .mostAchievedTime(monthlyStats.getMostActiveTime())
        .totalCount(monthlyStats.size())
        .build();
  }

  private DayOfWeekStatsDto createDayOfWeekStats(MonthlyStats monthlyStats) {
    Map<DayOfWeek, Integer> dayOfWeekStats = monthlyStats.collectDayOfWeek();
    List<DayOfWeek> mostActiveDays = getMostActiveDays(dayOfWeekStats);

    return DayOfWeekStatsDto.builder()
        .mostActiveDays(mostActiveDays)
        .stats(dayOfWeekStats)
        .build();
  }

  private List<DayOfWeek> getMostActiveDays(Map<DayOfWeek, Integer> stats) {
    int max = stats.values()
        .stream()
        .max(Integer::compareTo)
        .orElse(0);

    return stats.entrySet()
        .stream()
        .filter(entry -> entry.getValue() == max && max > 0)
        .map(Map.Entry::getKey)
        .toList();
  }

  private GenericCalendarStats<DduduCompletionResponse> getCalendarCompletions(
      YearMonth fromMonth,
      YearMonth toMonth,
      LocalDate from,
      LocalDate to,
      User user,
      Goal goal
  ) {
    if (toMonth.isAfter(fromMonth)) {
      return GenericCalendarStats.from(false, Collections.emptyList());
    }

    List<DduduCompletionResponse> completions = dduduStatsPort.calculateDdudusCompletion(
        from,
        to,
        user.getId(),
        goal.getId(),
        Collections.singletonList(PrivacyType.PUBLIC)
    );

    return GenericCalendarStats.from(true, completions);
  }

}
