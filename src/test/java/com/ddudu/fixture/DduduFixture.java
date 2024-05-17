package com.ddudu.fixture;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.old.todo.domain.Todo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static Todo createRandomDduduWithGoal(Goal goal) {
    return Todo.builder()
        .name(getRandomSentenceWithMax(50))
        .goal(goal)
        .user(goal.getUser())
        .build();
  }

}
