package com.ddudu.fixture;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static List<Ddudu> createMultipleDdudusWithGoal(Goal goal, int size) {
    List<Ddudu> ddudus = Lists.newArrayList();

    for (int i = 0; i < size; i++) {
      Ddudu ddudu = createDdudu(
          (long) (i + 1), goal.getId(), goal.getUserId(), getRandomSentenceWithMax(50), null, null,
          null, null, null, null
      );

      ddudus.add(ddudu);
    }

    return ddudus;
  }

  public static Ddudu createRandomDduduWithGoal(Goal goal) {
    return createRandomDduduWithReference(goal.getId(), goal.getUserId(), false, null);
  }

  public static Ddudu createRandomDduduWithReference(
      Long goalId, Long userId, Boolean isPostponed, DduduStatus status
  ) {
    return createDdudu(
        getRandomId(), goalId, userId, getRandomSentenceWithMax(50), status, null, null, null, null,
        isPostponed
    );
  }

  public static Ddudu createRandomDduduWithSchedule(Goal goal, LocalDate scheduledOn) {
    return createDdudu(
        getRandomId(), goal.getId(), goal.getUserId(), getRandomSentenceWithMax(50), null, null,
        scheduledOn, null, null, false
    );
  }

  public static Ddudu createDdudu(
      Long id, Long goalId, Long userId, String name, DduduStatus dduduStatus, String statusValue,
      LocalDate scheduledOn, LocalTime beginAt, LocalTime endAt, Boolean isPostponed
  ) {
    return Ddudu.builder()
        .id(id)
        .goalId(goalId)
        .userId(userId)
        .name(name)
        .isPostponed(isPostponed)
        .status(dduduStatus)
        .statusValue(statusValue)
        .scheduledOn(scheduledOn)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
