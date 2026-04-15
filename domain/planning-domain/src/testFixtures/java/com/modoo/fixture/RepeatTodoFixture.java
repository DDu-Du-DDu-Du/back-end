package com.modoo.fixture;

import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.modoo.domain.planning.repeattodo.aggregate.vo.DailyRepeatPattern;
import com.modoo.domain.planning.repeattodo.aggregate.vo.MonthlyRepeatPattern;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatPattern;
import com.modoo.domain.planning.repeattodo.aggregate.vo.WeeklyRepeatPattern;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepeatTodoFixture extends BaseFixture {

  public static RepeatTodo createRepeatTodo(
      RepeatType repeatType,
      RepeatPattern repeatPattern,
      LocalDate startDate,
      LocalDate endDate
  ) {
    return RepeatTodo.builder()
        .goalId(GoalFixture.getRandomId())
        .name(getRandomSentenceWithMax(50))
        .repeatType(repeatType)
        .repeatPattern(repeatPattern)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

  public static RepeatTodo createRepeatTodoWithGoal(
      Goal goal,
      LocalDate startDate,
      LocalDate endDate
  ) {
    RepeatType repeatType = getRandomRepeatType();

    return RepeatTodo.builder()
        .goalId(goal.getId())
        .name(getRandomSentenceWithMax(50))
        .repeatType(repeatType)
        .repeatPattern(createRandomRepeatPattern(repeatType))
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

  public static RepeatTodo createDailyRepeatTodoWithGoal(Goal goal) {
    return RepeatTodo.builder()
        .goalId(goal.getId())
        .name(getRandomSentenceWithMax(50))
        .repeatType(RepeatType.DAILY)
        .repeatPattern(createRandomRepeatPattern(RepeatType.DAILY))
        .startDate(LocalDate.now())
        .endDate(LocalDate.now()
            .plusDays(7))
        .build();
  }

  public static RepeatType getRandomRepeatType() {
    RepeatType[] types = RepeatType.values();
    int index = getRandomInt(0, types.length - 1);

    return types[index];
  }

  public static RepeatPattern createRandomRepeatPattern(RepeatType type) {
    int lastDay = YearMonth.now()
        .atEndOfMonth()
        .getDayOfMonth();
    return switch (type) {
      case DAILY -> createDailyRepeatPattern();
      case WEEKLY -> createWeeklyRepeatPattern(getRandomRepeatDaysOfWeek());
      case MONTHLY -> createMonthlyRepeatPattern(
          getRandomRepeatDaysOfMonth(1, lastDay), false
      );
    };
  }

  public static RepeatPattern createDailyRepeatPattern() {
    return new DailyRepeatPattern();
  }

  public static RepeatPattern createWeeklyRepeatPattern(List<String> repeatDaysOfWeek) {
    return new WeeklyRepeatPattern(repeatDaysOfWeek);
  }

  public static RepeatPattern createMonthlyRepeatPattern(
      List<Integer> repeatDaysOfMonth,
      boolean lastDayOfMonth
  ) {
    return new MonthlyRepeatPattern(repeatDaysOfMonth, lastDayOfMonth);
  }

  public static List<String> getRandomRepeatDaysOfWeek() {
    int numberOfDaysToRepeat = getRandomInt(1, DayOfWeek.values().length);
    return getRandomRepeatDaysOfWeek(numberOfDaysToRepeat);
  }

  public static List<String> getRandomRepeatDaysOfWeek(int numberOfDaysToRepeat) {
    List<DayOfWeek> days = Arrays.stream(DayOfWeek.values())
        .collect(Collectors.toList());

    Collections.shuffle(days);

    return days.stream()
        .limit(numberOfDaysToRepeat)
        .map(DayOfWeek::name)
        .toList();
  }

  public static List<Integer> getRandomRepeatDaysOfMonth(int numberOfDaysToRepeat) {
    int to = YearMonth.now()
        .atEndOfMonth()
        .getDayOfMonth();

    return getRandomRepeatDaysOfMonth(1, to, numberOfDaysToRepeat);
  }

  public static List<Integer> getRandomRepeatDaysOfMonth(int from, int to) {
    int numberOfDaysToRepeat = getRandomInt(from, to);
    return getRandomRepeatDaysOfMonth(from, to, numberOfDaysToRepeat);
  }

  public static List<Integer> getRandomRepeatDaysOfMonth(
      int from,
      int to,
      int numberOfDaysToRepeat
  ) {
    List<Integer> days = IntStream.rangeClosed(from, to)
        .boxed()
        .collect(Collectors.toList());

    Collections.shuffle(days);

    return days.stream()
        .limit(numberOfDaysToRepeat)
        .toList();
  }

}
