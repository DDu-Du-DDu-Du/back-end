package com.ddudu.fixture;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static Ddudu createRandomDduduWithGoal(Goal goal) {
    return createRandomDduduWithReference(goal.getId(), goal.getUserId());
  }

  public static Ddudu createRandomDduduWithReference(Long goalId, Long userId) {
    return createDdudu(
        getRandomId(), goalId, userId, getRandomSentenceWithMax(50), null, null, null, null,
        false
    );
  }

  public static Ddudu createRandomDduduWithSchedule(Goal goal, LocalDate scheduledOn) {
    return createDdudu(
        getRandomId(), goal.getId(), goal.getUserId(), getRandomSentenceWithMax(50), null,
        scheduledOn, null, null, false
    );
  }

  public static Ddudu createDdudu(
      Long id, Long goalId, Long userId, String name, DduduStatus dduduStatus,
      LocalDate scheduledOn, LocalTime beginAt, LocalTime endAt, Boolean isPostponed
  ) {
    return Ddudu.builder()
        .id(id)
        .goalId(goalId)
        .userId(userId)
        .name(name)
        .isPostponed(isPostponed)
        .status(dduduStatus)
        .scheduledOn(scheduledOn)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
