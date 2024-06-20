package com.ddudu.fixture;

import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodGoalFixture extends BaseFixture {

  public static PeriodGoalType getRandomType() {
    PeriodGoalType[] types = PeriodGoalType.values();
    int index = getRandomInt(0, types.length - 1);

    return types[index];
  }

}
