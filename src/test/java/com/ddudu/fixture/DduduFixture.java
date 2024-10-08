package com.ddudu.fixture;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.Ddudu.DduduBuilder;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static List<Ddudu> createDifferentDdudusWithGoal(
      Goal goal, int completedCount, int uncompletedCount
  ) {
    List<Ddudu> ddudus = Lists.newArrayList();

    ddudus.addAll(createMultipleDdudusWithGoal(goal, completedCount));
    ddudus.addAll(createMultipleDdudusWithGoal(goal, uncompletedCount, DduduStatus.UNCOMPLETED));

    return ddudus;
  }

  public static List<Ddudu> createMultipleDdudusWithGoal(Goal goal, int size) {
    return createMultipleDdudusWithGoal(goal, size, DduduStatus.COMPLETE);
  }

  private static List<Ddudu> createMultipleDdudusWithGoal(Goal goal, int size, DduduStatus status) {
    List<Ddudu> ddudus = Lists.newArrayList();

    for (int i = 0; i < size; i++) {
      ddudus.add(createRandomDduduWithStatus(goal, status));
    }

    return ddudus;
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
      Long goalId, Long userId, Boolean isPostponed, DduduStatus status
  ) {
    return getDduduBuilder()
        .goalId(goalId)
        .userId(userId)
        .isPostponed(isPostponed)
        .status(status)
        .build();
  }

  public static Ddudu createRandomDduduWithStatusAndSchedule(
      Goal goal, DduduStatus status, LocalDate scheduledOn
  ) {
    return getDduduBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .status(status)
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Ddudu createRandomDduduWithSchedule(Goal goal, LocalDate scheduledOn) {
    return getDduduBuilder()
        .goalId(goal.getId())
        .userId(goal.getUserId())
        .scheduledOn(scheduledOn)
        .build();
  }

  public static Ddudu createRandomDduduWithGoalAndTime(
      Goal goal, LocalTime beginAt, LocalTime endAt
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

  public static DduduBuilder getDduduBuilder(
  ) {
    return Ddudu.builder()
        .id(getRandomId())
        .name(getRandomSentenceWithMax(50))
        .scheduledOn(LocalDate.now());
  }

}
