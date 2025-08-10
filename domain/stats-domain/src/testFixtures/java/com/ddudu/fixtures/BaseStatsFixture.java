package com.ddudu.fixtures;

import com.ddudu.aggregate.BaseStats;
import com.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.fixture.BaseFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BaseStatsFixture extends BaseFixture {

  public static BaseStats createAmOnlyStat(Long goalId, LocalDate scheduledOn) {
    LocalTime beginAt = getRandomAm();
    LocalTime endAt = getFutureTimeFrom(beginAt);

    return createWithGoalStatusScheduledAndTimes(
        goalId,
        DduduStatus.COMPLETE,
        false,
        scheduledOn,
        beginAt,
        endAt
    );
  }

  public static BaseStats createPmOnlyStat(Long goalId, LocalDate scheduledOn) {
    LocalTime beginAt = getRandomPm();
    LocalTime endAt = getFutureTimeFrom(beginAt);

    return createWithGoalStatusScheduledAndTimes(
        goalId,
        DduduStatus.COMPLETE,
        false,
        scheduledOn,
        beginAt,
        endAt
    );
  }

  public static BaseStats createAcrossNoonBalancedStat(Long goalId, LocalDate scheduledOn) {
    return createWithGoalStatusScheduledAndTimes(
        goalId,
        DduduStatus.COMPLETE,
        false,
        scheduledOn,
        LocalTime.of(11, 0),
        LocalTime.of(13, 0)
    );
  }

  public static BaseStats createNoonZeroStat(Long goalId, LocalDate scheduledOn) {
    return createWithGoalStatusScheduledAndTimes(
        goalId,
        DduduStatus.COMPLETE,
        false,
        scheduledOn,
        LocalTime.NOON,
        LocalTime.NOON
    );
  }

  public static BaseStats createWithGoalStatusScheduledAndTimes(
      Long goalId,
      DduduStatus status,
      boolean isPostponed,
      LocalDate scheduledOn,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    Long dduduId = getRandomId();

    return createBaseStats(dduduId, goalId, status, isPostponed, scheduledOn, beginAt, endAt);
  }

  public static List<BaseStats> createPostponedCompleteStats(Long goalId, int size) {
    List<BaseStats> stats = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      stats.add(createRandomWithGoalAndPostponedAndStatus(goalId, true, DduduStatus.COMPLETE));
    }

    return stats;
  }

  public static List<BaseStats> createConsecutiveCompletedStats(
      Long goalId,
      LocalDate from,
      int size
  ) {
    List<BaseStats> stats = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      LocalDate scheduledOn = from.plusDays(i);

      stats.add(
          createRandomWithGoalAndPostponedAndStatusAndScheduled(
              goalId,
              false,
              DduduStatus.COMPLETE,
              scheduledOn
          )
      );
    }

    return stats;
  }

  public static List<BaseStats> createPostponedStats(Long goalId, int size) {
    List<BaseStats> stats = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      stats.add(createRandomPostponedWithGoal(goalId));
    }

    return stats;
  }

  public static List<BaseStats> createNotPostponedStats(Long goalId, int size) {
    List<BaseStats> stats = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      stats.add(createRandomNotPostponedWithGoal(goalId));
    }

    return stats;
  }

  public static List<BaseStats> createCompletedStats(Long goalId, int size) {
    List<BaseStats> stats = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      stats.add(createRandomCompleteWithGoal(goalId));
    }

    return stats;
  }

  public static List<BaseStats> createUncompletedStats(Long goalId, int size) {
    List<BaseStats> stats = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      stats.add(createRandomUncompletedWithGoal(goalId));
    }

    return stats;
  }

  public static BaseStats createRandom() {
    Long goalId = getRandomId();

    return createRandomWithGoal(goalId);
  }

  public static BaseStats createRandomWithGoal(Long goalId) {
    boolean isPostponed = faker.bool()
        .bool();

    return createRandomWithGoalAndPostponed(goalId, isPostponed);
  }

  public static BaseStats createRandomPostponedWithGoal(Long goalId) {
    return createRandomWithGoalAndPostponed(goalId, true);
  }

  public static BaseStats createRandomNotPostponedWithGoal(Long goalId) {
    return createRandomWithGoalAndPostponed(goalId, false);
  }

  public static BaseStats createRandomUncompletedWithGoal(Long goalId) {
    return createRandomWithGoalAndStatus(goalId, DduduStatus.UNCOMPLETED);
  }

  public static BaseStats createRandomCompleteWithGoal(Long goalId) {
    return createRandomWithGoalAndStatus(goalId, DduduStatus.COMPLETE);
  }

  public static BaseStats createRandomWithGoalAndStatus(Long goalId, DduduStatus status) {
    boolean isPostponed = faker.bool()
        .bool();

    return createRandomWithGoalAndPostponedAndStatus(goalId, isPostponed, status);
  }

  public static BaseStats createRandomWithGoalAndPostponed(Long goalId, boolean isPostponed) {
    List<String> names = Arrays.stream(DduduStatus.values())
        .map(Enum::name)
        .toList();
    int statusIndexMax = DduduStatus.values().length - 1;
    int randomIndex = getRandomInt(0, statusIndexMax);
    DduduStatus status = DduduStatus.from(names.get(randomIndex));

    return createRandomWithGoalAndPostponedAndStatus(goalId, isPostponed, status);
  }

  public static BaseStats createRandomWithGoalAndPostponedAndStatus(
      Long goalId,
      boolean isPostponed,
      DduduStatus status
  ) {
    YearMonth yearMonth = YearMonth.now();

    return createRandomWithGoalAndPostponedAndStatusInMonth(
        goalId,
        isPostponed,
        status,
        yearMonth
    );
  }

  public static BaseStats createRandomWithGoalAndPostponedAndStatusInMonth(
      Long goalId,
      boolean isPostponed,
      DduduStatus status,
      YearMonth yearMonth
  ) {
    LocalDateTime from = yearMonth.atDay(1)
        .atStartOfDay();
    LocalDateTime to = yearMonth.atEndOfMonth()
        .atTime(LocalTime.MAX);
    LocalDate scheduledOn = getRandomDateTimeBetween(from, to)
        .toLocalDate();

    return createRandomWithGoalAndPostponedAndStatusAndScheduled(
        goalId,
        isPostponed,
        status,
        scheduledOn
    );
  }

  public static BaseStats createRandomWithGoalAndPostponedAndStatusAndScheduled(
      Long goalId,
      boolean isPostponed,
      DduduStatus status,
      LocalDate scheduledOn
  ) {
    Long dduduId = getRandomId();
    LocalTime beginAt = getPastTime();
    LocalTime endAt = getFutureTime();

    return createBaseStats(dduduId, goalId, status, isPostponed, scheduledOn, beginAt, endAt);
  }

  private static BaseStats createBaseStats(
      Long dduduId,
      Long goalId,
      DduduStatus status,
      boolean isPostponed,
      LocalDate scheduledOn,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    return BaseStats.builder()
        .dduduId(dduduId)
        .goalId(goalId)
        .goalName(goalId.toString())
        .status(status)
        .scheduledOn(scheduledOn)
        .isPostponed(isPostponed)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
