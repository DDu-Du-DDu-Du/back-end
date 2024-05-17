package com.ddudu.fixture;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DduduFixture extends BaseFixture {

  public static Ddudu createRandomDduduWithGoal(Goal goal) {
    return createDdudu(
        getRandomId(), goal, goal.getUser(), getRandomSentenceWithMax(50), null, null, null, false);
  }

  public static Ddudu createDdudu(
      Long id, Goal goal, User user, String name, DduduStatus dduduStatus, LocalDateTime beginAt,
      LocalDateTime endAt, Boolean isPostPoned
  ) {
    return Ddudu.builder()
        .id(id)
        .goal(goal)
        .user(user)
        .name(name)
        .isPostponed(isPostPoned)
        .status(dduduStatus)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
