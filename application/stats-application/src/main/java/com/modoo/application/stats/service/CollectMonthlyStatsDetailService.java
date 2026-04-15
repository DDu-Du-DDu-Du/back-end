package com.modoo.application.stats.service;

import com.modoo.aggregate.MonthlyStats;
import com.modoo.application.common.dto.stats.AchievedDetailOverviewDto;
import com.modoo.application.common.dto.stats.DayOfWeekStatsDto;
import com.modoo.application.common.dto.stats.GenericCalendarStats;
import com.modoo.application.common.dto.stats.PostponedDetailOverviewDto;
import com.modoo.application.common.dto.stats.RepeatTodoStatsDto;
import com.modoo.application.common.dto.stats.response.AchievedStatsDetailResponse;
import com.modoo.application.common.dto.stats.response.PostponedStatsDetailResponse;
import com.modoo.application.common.dto.stats.response.TodoCompletionResponse;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.stats.in.CollectMonthlyStatsDetailUseCase;
import com.modoo.application.common.port.stats.out.MonthlyStatsPort;
import com.modoo.application.common.port.stats.out.TodoStatsPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.StatsErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.user.user.aggregate.User;
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
public class CollectMonthlyStatsDetailService implements CollectMonthlyStatsDetailUseCase {

  private static final boolean IS_ACHIEVED = true;
  private static final boolean IS_POSTPONED = false;

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final MonthlyStatsPort monthlyStatsPort;
  private final TodoStatsPort todoStatsPort;

  @Override
  public AchievedStatsDetailResponse collectAchievedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  ) {
    final User user = userLoaderPort.getUserOrElseThrow(
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
    AchievedDetailOverviewDto overview = createAchievedOverview(reduced);
    DayOfWeekStatsDto dayOfWeekStats = createDayOfWeekStats(reduced, IS_ACHIEVED);
    List<RepeatTodoStatsDto> repeatTodoCounts = monthlyStatsPort.countRepeatTodo(
        user.getId(),
        goal.getId(),
        from,
        to
    );
    GenericCalendarStats<TodoCompletionResponse> completions = getCalendarCompletions(
        fromMonth,
        toMonth,
        from,
        to,
        user,
        goal,
        IS_ACHIEVED
    );

    return AchievedStatsDetailResponse.builder()
        .overview(overview)
        .dayOfWeekStats(dayOfWeekStats)
        .repeatTodoStats(repeatTodoCounts)
        .goalId(goal.getId())
        .goalColor(goal.getColor())
        .calendarStats(completions)
        .build();
  }

  @Override
  public PostponedStatsDetailResponse collectPostponedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  ) {
    final User user = userLoaderPort.getUserOrElseThrow(
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
    MonthlyStats reduced = collectPostponedStatsAtOnce(user.getId(), goal, fromMonth, to);
    PostponedDetailOverviewDto overview = createPostponedOverview(reduced);
    DayOfWeekStatsDto dayOfWeekStats = createDayOfWeekStats(reduced, IS_POSTPONED);
    GenericCalendarStats<TodoCompletionResponse> calendarStats = getCalendarCompletions(
        fromMonth,
        toMonth,
        from,
        to,
        user,
        goal,
        IS_POSTPONED
    );

    return PostponedStatsDetailResponse.builder()
        .overview(overview)
        .goalId(goal.getId())
        .goalColor(goal.getColor())
        .calendarStats(calendarStats)
        .dayOfWeekStats(dayOfWeekStats)
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

  private MonthlyStats collectPostponedStatsAtOnce(
      Long userId,
      Goal goal,
      YearMonth fromMonth,
      LocalDate to
  ) {
    return monthlyStatsPort.collectMonthlyPostponedStats(userId, goal, fromMonth.atDay(1), to)
        .values()
        .stream()
        .reduce(MonthlyStats.empty(userId, fromMonth), MonthlyStats::merge);
  }

  private AchievedDetailOverviewDto createAchievedOverview(MonthlyStats monthlyStats) {
    return AchievedDetailOverviewDto.builder()
        .achievementRate(monthlyStats.calculateAchievementRate())
        .achievementCount(monthlyStats.countAchievements())
        .mostAchievedTime(monthlyStats.getMostActiveTime())
        .totalCount(monthlyStats.size())
        .build();
  }

  private PostponedDetailOverviewDto createPostponedOverview(MonthlyStats monthlyStats) {
    return PostponedDetailOverviewDto.builder()
        .postponedCount(monthlyStats.calculatePostponementCount())
        .postponementRate(monthlyStats.calculatePostponementRate())
        .reattainedCount(monthlyStats.calculateReattainmentCount())
        .totalCount(monthlyStats.size())
        .reattainmentRate(monthlyStats.calculateReattainmentRate())
        .build();
  }

  private DayOfWeekStatsDto createDayOfWeekStats(MonthlyStats monthlyStats, boolean isAchieved) {
    Map<DayOfWeek, Integer> dayOfWeekStats = monthlyStats.collectDayOfWeek(isAchieved);
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

  private GenericCalendarStats<TodoCompletionResponse> getCalendarCompletions(
      YearMonth fromMonth,
      YearMonth toMonth,
      LocalDate from,
      LocalDate to,
      User user,
      Goal goal,
      boolean isAchieved
  ) {
    if (toMonth.isAfter(fromMonth)) {
      return GenericCalendarStats.from(false, Collections.emptyList());
    }

    List<TodoCompletionResponse> completions = todoStatsPort.calculateTodosCompletion(
        from,
        to,
        user.getId(),
        goal.getId(),
        Collections.singletonList(PrivacyType.PUBLIC),
        isAchieved
    );

    return GenericCalendarStats.from(true, completions);
  }

}
