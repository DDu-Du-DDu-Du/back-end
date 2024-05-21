package com.ddudu.fixture;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.todo.domain.Todo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static Todo createRandomDduduWithGoal(Goal goal, User user) {
    return Todo.builder()
        .name(getRandomSentenceWithMax(50))
        .goal(goal)
        .user(
            // TODO: change this to goal.getId() after migration ddudu as left for avoidance of compile errors
            user
        )
        .build();
  }

}
