package com.ddudu.fixture;

import com.ddudu.application.domain.repeat_ddudu.domain.DailyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.MonthlyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.WeeklyRepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.enums.RepeatType;
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
      RepeatType repeatType, RepeatPattern repeatPattern, LocalDate startDate, LocalDate endDate
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
    return WeeklyRepeatPattern.withValidation(repeatDaysOfWeek);
  }

  public static RepeatPattern createMonthlyRepeatPattern(
      List<Integer> repeatDaysOfMonth, boolean lastDayOfMonth
  ) {
    return MonthlyRepeatPattern.withValidation(repeatDaysOfMonth, lastDayOfMonth);
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
