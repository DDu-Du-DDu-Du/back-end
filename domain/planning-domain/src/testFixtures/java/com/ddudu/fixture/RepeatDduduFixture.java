package com.ddudu.fixture;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.DailyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.MonthlyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.RepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.WeeklyRepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepeatDduduFixture extends BaseFixture {

  public static RepeatDdudu createRepeatDdudu(
      RepeatType repeatType,
      RepeatPattern repeatPattern,
      LocalDate startDate,
      LocalDate endDate
  ) {
    return RepeatDdudu.builder()
        .goalId(GoalFixture.getRandomId())
        .name(getRandomSentenceWithMax(50))
        .repeatType(repeatType)
        .repeatPattern(repeatPattern)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

  public static RepeatDdudu createRepeatDduduWithGoal(
      Goal goal,
      LocalDate startDate,
      LocalDate endDate
  ) {
    RepeatType repeatType = getRandomRepeatType();

    return RepeatDdudu.builder()
        .goalId(goal.getId())
        .name(getRandomSentenceWithMax(50))
        .repeatType(repeatType)
        .repeatPattern(createRandomRepeatPattern(repeatType))
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

  public static RepeatDdudu createDailyRepeatDduduWithGoal(Goal goal) {
    return RepeatDdudu.builder()
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
    return switch (type) {
      case DAILY -> createDailyRepeatPattern();
      case WEEKLY -> createWeeklyRepeatPattern(getRandomRepeatDaysOfWeek());
      case MONTHLY -> createMonthlyRepeatPattern(
          getRandomRepeatDaysOfMonth(), false
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
      List<Integer> repeatDaysOfMonth, boolean lastDayOfMonth
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

  public static List<Integer> getRandomRepeatDaysOfMonth() {
    int numberOfDaysToRepeat = getRandomInt(1, 31);
    return getRandomRepeatDaysOfMonth(numberOfDaysToRepeat);
  }

  public static List<Integer> getRandomRepeatDaysOfMonth(int numberOfDaysToRepeat) {
    List<Integer> days = IntStream.rangeClosed(1, 31)
        .boxed()
        .collect(Collectors.toList());

    Collections.shuffle(days);

    return days.stream()
        .limit(numberOfDaysToRepeat)
        .toList();
  }

}
