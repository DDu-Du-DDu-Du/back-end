package com.ddudu.application.domain.goal.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.domain.user.domain.User;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class GoalDomainService {

  public Goal create(User user, CreateGoalRequest request) {
    return Goal.builder()
        .userId(user.getId())
        .name(request.name())
        .privacyType(PrivacyType.from(request.privacyType()))
        .color(request.color())
        .build();
  }

}
