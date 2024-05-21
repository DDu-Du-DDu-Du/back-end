package com.ddudu.fixture;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
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
    return Goal.builder()
        .name(getRandomSentenceWithMax(50))
        .userId(UserFixture.getRandomId())
        .color(getRandomColor())
        .privacyType(getRandomPrivacyType())
        .build();
  }

  public static Goal createRandomGoalWithUser(User user) {
    return Goal.builder()
        .name(getRandomSentenceWithMax(50))
        .userId(user.getId())
        .color(getRandomColor())
        .privacyType(getRandomPrivacyType())
        .build();
  }

}
