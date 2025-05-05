package com.ddudu.domain.planning.goal.service;

import com.ddudu.domain.common.annotation.DomainService;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.goal.dto.request.CreateGoalRequest;
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
