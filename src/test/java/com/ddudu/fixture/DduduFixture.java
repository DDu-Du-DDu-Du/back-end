package com.ddudu.fixture;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static Ddudu createRandomDduduWithReference(Long goalId, Long userId) {
    return createDdudu(
        getRandomId(), goalId, userId, getRandomSentenceWithMax(50), null, null, null,
        false
    );
  }

  public static Ddudu createRandomDduduWithGoal(Goal goal) {
    return createDdudu(
        getRandomId(), goal.getId(), goal.getUserId(), getRandomSentenceWithMax(50), null, null,
        null, false
    );
  }

  public static Ddudu createRandomDduduWithGoalAndTime(
      Goal goal, LocalTime beginAt, LocalTime endAt
  ) {
    return createDdudu(
        getRandomId(), goal.getId(), goal.getUserId(), getRandomSentenceWithMax(50), null, beginAt,
        endAt, false
    );
  }

  public static Ddudu createDdudu(
      Long id, Long goalId, Long userId, String name, DduduStatus dduduStatus, LocalTime beginAt,
      LocalTime endAt, Boolean isPostponed
  ) {
    return Ddudu.builder()
        .id(id)
        .goalId(goalId)
        .userId(userId)
        .name(name)
        .isPostponed(isPostponed)
        .status(dduduStatus)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
