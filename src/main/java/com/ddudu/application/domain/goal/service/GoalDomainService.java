package com.ddudu.application.domain.goal.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class GoalDomainService {

  public Goal create(User user, String name, String privacyType, String color) {
    return Goal.builder()
        .user(user)
        .name(name)
        .privacyType(PrivacyType.from(privacyType))
        .color(color)
        .build();
  }

}
