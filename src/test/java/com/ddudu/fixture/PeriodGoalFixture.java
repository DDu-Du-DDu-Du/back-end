package com.ddudu.fixture;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.user.domain.User;
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

  public static PeriodGoal createRandomPeriodGoal(User user) {
    return PeriodGoal.builder()
        .id(getRandomId())
        .userId(user.getId())
        .type(getRandomType().name())
        .contents(getRandomSentenceWithMax(255))
        .planDate(LocalDate.now())
        .build();
  }

}
