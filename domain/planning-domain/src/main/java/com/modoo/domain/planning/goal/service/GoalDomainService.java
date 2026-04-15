package com.modoo.domain.planning.goal.service;

import com.modoo.common.annotation.DomainService;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.planning.goal.dto.CreateGoalCommand;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class GoalDomainService {

  private static final String DEFAULT_GOAL_NAME = "목표";
  private static final int DEFAULT_GOAL_COUNT = 3;

  public Goal create(Long userId, CreateGoalCommand command) {
    return Goal.builder()
        .userId(userId)
        .name(command.name())
        .privacyType(PrivacyType.from(command.privacyType()))
        .color(command.color())
        .priority(command.priority())
        .build();
  }

  public List<Goal> createDefaultGoals(Long userId) {
    List<Goal> goals = new ArrayList<>();

    for (int idx = 1; idx <= DEFAULT_GOAL_COUNT; ++idx) {
      goals.add(createDefaultGoalWithName(userId, DEFAULT_GOAL_NAME + " " + idx, idx));
    }

    return goals;
  }

  private Goal createDefaultGoalWithName(Long userId, String name, int priority) {
    return Goal.builder()
        .userId(userId)
        .name(name)
        .privacyType(PrivacyType.PUBLIC)
        .priority(priority)
        .build();
  }

}
