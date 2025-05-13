package com.ddudu.fixture;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalFixture extends BaseFixture {

  public static PrivacyType getRandomPrivacyType() {
    PrivacyType[] types = PrivacyType.values();
    int index = getRandomInt(0, types.length - 1);

    return types[index];
  }

  public static GoalStatus getRandomGoalStatus() {
    GoalStatus[] types = GoalStatus.values();
    int index = getRandomInt(0, types.length - 1);

    return types[index];
  }

  public static Goal createRandomGoal() {
    return getGoalBuilder()
        .build();
  }

  public static Goal createRandomGoalWithUser(Long userId) {
    return getGoalBuilder()
        .userId(userId)
        .build();
  }

  public static List<Goal> createRandomGoalsWithUser(Long userId, int size) {
    List<Goal> goals = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      goals.add(createRandomGoalWithUser(userId));
    }

    return goals;
  }

  public static Goal createRandomGoalWithUserAndPrivacyType(Long userId, PrivacyType privacyType) {
    return getGoalBuilder()
        .userId(userId)
        .privacyType(privacyType)
        .build();
  }

  private static Goal.GoalBuilder getGoalBuilder() {
    return Goal.builder()
        .name(getRandomSentenceWithMax(50))
        .userId(getRandomId())
        .color(getRandomColor())
        .privacyType(getRandomPrivacyType());
  }

}
