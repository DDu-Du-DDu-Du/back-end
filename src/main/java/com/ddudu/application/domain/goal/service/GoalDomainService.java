package com.ddudu.application.domain.goal.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class GoalDomainService {

  private final String DEFAULT_GOAL_NAME = "목표";

  public Goal create(User user, CreateGoalRequest request) {
    return Goal.builder()
        .userId(user.getId())
        .name(request.name())
        .privacyType(PrivacyType.from(request.privacyType()))
        .color(request.color())
        .build();
  }

  public Goal createDefaultGoal(User user) {
    return Goal.builder()
        .userId(user.getId())
        .name(DEFAULT_GOAL_NAME)
        .privacyType(PrivacyType.PUBLIC)
        .build();
  }

}
