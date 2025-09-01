package com.ddudu.fixture;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu.DduduBuilder;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static List<Ddudu> createReattainedDdudus(
      Goal goal,
      int reattainedCount,
      int totalPostponedCount
  ) {
    List<Ddudu> ddudus = new ArrayList<>();

    for (int i = 0; i < reattainedCount; i++) {
      ddudus.add(createRandomDduduWithReference(
          goal.getId(),
          goal.getUserId(),
          true,
          DduduStatus.COMPLETE
      ));
    }

    for (int i = reattainedCount; i < totalPostponedCount; i++) {
      ddudus.add(createRandomDduduWithReference(
          goal.getId(),
          goal.getUserId(),
          true,
          DduduStatus.UNCOMPLETED
      ));
    }

    return ddudus;
  }

  public static List<Ddudu> createDdudusWithPostponedFlag(
      Goal goal,
      int postponedCount,
      int notPostponedCount
  ) {
    List<Ddudu> ddudus = new ArrayList<>();

    for (int i = 0; i < postponedCount; i++) {
      LocalDate scheduledOn = YearMonth.now()
          .atDay(getRandomInt(
              1,
              YearMonth.now()
                  .lengthOfMonth()
          ));

      ddudus.add(createDduduWithScheduleAndPostponedFlag(goal, true, scheduledOn));
    }

    for (int i = 0; i < notPostponedCount; i++) {
      LocalDate scheduledOn = YearMonth.now()
          .atDay(getRandomInt(
              1,
              YearMonth.now()
                  .lengthOfMonth()
          ));

      ddudus.add(createDduduWithScheduleAndPostponedFlag(goal, false, scheduledOn));
    }

    return ddudus;
  }

  public static List<Ddudu> createConsecutiveCompletedDdudus(Goal goal, int count) {
    LocalDate firstDate = YearMonth.now()
        .atDay(1);
    List<Ddudu> ddudus = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      LocalDate scheduled = firstDate.plusDays(i);
      ddudus.add(createRandomDduduWithStatusAndSchedule(goal, DduduStatus.COMPLETE, scheduled));
    }

    return ddudus;
  }

  public static List<Ddudu> createDifferentDdudusWithGoal(
      Goal goal,
      int completedCount,
      int uncompletedCount
  ) {
    List<Ddudu> ddudus = new ArrayList<>();

    ddudus.addAll(createMultipleDdudusWithGoal(goal, completedCount));
    ddudus.addAll(createMultipleDdudusWithGoal(goal, uncompletedCount, DduduStatus.UNCOMPLETED));

    return ddudus;
  }

  public static List<Ddudu> createMultipleDdudusWithGoal(Goal goal, int size) {
    return createMultipleDdudusWithGoal(goal, size, DduduStatus.COMPLETE);
  }

  private static List<Ddudu> createMultipleDdudusWithGoal(Goal goal, int size, DduduStatus status) {
    List<Ddudu> ddudus = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      ddudus.add(createRandomDduduWithStatus(goal, status));
    }

    return ddudus;
  }

  public static Ddudu createDduduWithScheduleAndPostponedFlag(
      Goal goal,
      boolean isPostponed,
      LocalDate scheduledOn
  ) {
    return getDduduBuilder()
        .userId(goal.getUserId())
        .goalId(goal.getId())
        .isPostponed(isPostponed)
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Ddudu createRandomDduduWithStatus(Goal goal, DduduStatus status) {
    return getDduduBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .status(status)
        .build();
  }

  public static Ddudu createRandomDduduWithGoal(Goal goal) {
    return getDduduBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .build();
  }

  public static Ddudu createRandomDduduWithReference(
      Long goalId,
      Long userId,
      Boolean isPostponed,
      DduduStatus status
  ) {
    return getDduduBuilder()
        .goalId(goalId)
        .userId(userId)
        .isPostponed(isPostponed)
        .status(status)
        .build();
  }

  public static Ddudu createRandomDduduWithStatusAndSchedule(
      Goal goal,
      DduduStatus status,
      LocalDate scheduledOn
  ) {
    return getDduduBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .status(status)
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Ddudu createRandomDduduWithSchedule(
      Long userId,
      Long goalId,
      LocalDate scheduledOn
  ) {
    return getDduduBuilder()
        .goalId(goalId)
        .userId(userId)
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Ddudu createRandomDduduWithGoalAndTime(
      Goal goal,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    return getDduduBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public static Ddudu createRandomDduduWithRepeatDdudu(Long userId, RepeatDdudu repeatDdudu) {
    return getDduduBuilder()
        .goalId(repeatDdudu.getGoalId())
        .userId(userId)
        .repeatDduduId(repeatDdudu.getId())
        .build();
  }

  public static DduduBuilder getDduduBuilder() {
    return Ddudu.builder()
        .id(getRandomId())
        .name(getRandomSentenceWithMax(50))
        .scheduledOn(LocalDate.now());
  }

}
