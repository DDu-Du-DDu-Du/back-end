package com.ddudu.application.domain.goal.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class GoalDomainService {

  private final String DEFAULT_GOAL_NAME = "목표";
  private static final int DEFAULT_GOAL_COUNT = 3;

  public Goal create(User user, CreateGoalRequest request) {
    return Goal.builder()
        .userId(user.getId())
        .name(request.name())
        .privacyType(PrivacyType.from(request.privacyType()))
        .color(request.color())
        .build();
  }

  public List<Goal> createDefaultGoals(User user) {
    List<Goal> goals = new ArrayList<>();
    for (int idx = 1; idx <= DEFAULT_GOAL_COUNT; ++idx) {
      goals.add(createDefaultGoalWithName(user, DEFAULT_GOAL_NAME + " " + idx));
    }

    return goals;
  }

  private Goal createDefaultGoalWithName(User user, String name) {
    return Goal.builder()
        .userId(user.getId())
        .name(name)
        .privacyType(PrivacyType.PUBLIC)
        .build();
  }

}
