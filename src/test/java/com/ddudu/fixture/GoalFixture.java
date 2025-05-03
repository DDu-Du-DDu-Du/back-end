package com.ddudu.fixture;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.google.common.collect.Lists;
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

  public static Goal createRandomGoalWithUser(User user) {
    return getGoalBuilder()
        .userId(user.getId())
        .build();
  }

  public static List<Goal> createRandomGoalsWithUser(User user, int size) {
    List<Goal> goals = Lists.newArrayList();

    for (int i = 0; i < size; i++) {
      goals.add(createRandomGoalWithUser(user));
    }

    return goals;
  }

  public static Goal createRandomGoalWithUserAndPrivacyType(User user, PrivacyType privacyType) {
    return getGoalBuilder()
        .userId(user.getId())
        .privacyType(privacyType)
        .build();
  }

  private static Goal.GoalBuilder getGoalBuilder() {
    return Goal.builder()
        .name(getRandomSentenceWithMax(50))
        .userId(UserFixture.getRandomId())
        .color(getRandomColor())
        .privacyType(getRandomPrivacyType());
  }

}
