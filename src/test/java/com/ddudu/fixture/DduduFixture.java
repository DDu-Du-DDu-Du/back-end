package com.ddudu.fixture;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static Ddudu createRandomDduduWithGoal(Goal goal) {
    return Ddudu.builder()
        .name(getRandomSentenceWithMax(50))
        .goal(goal)
        .user(goal.getUser())
        .build();
  }

}
