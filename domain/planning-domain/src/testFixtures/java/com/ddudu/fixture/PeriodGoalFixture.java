package com.ddudu.fixture;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodGoalFixture extends BaseFixture {

  public static PeriodGoalType getRandomType() {
    PeriodGoalType[] types = PeriodGoalType.values();
    int index = getRandomInt(0, types.length - 1);

    return types[index];
  }

  public static PeriodGoal createPeriodGoal(
      Long userId,
      String contents,
      PeriodGoalType type,
      LocalDate date
  ) {
    return PeriodGoal.builder()
        .userId(userId)
        .contents(contents)
        .type(type)
        .planDate(date)
        .build();
  }

  public static PeriodGoal createRandomPeriodGoal(Long userId) {
    return PeriodGoal.builder()
        .id(getRandomId())
        .userId(userId)
        .type(getRandomType())
        .contents(getRandomSentenceWithMax(255))
        .planDate(LocalDate.now())
        .build();
  }

}
