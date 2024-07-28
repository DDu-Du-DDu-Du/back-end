package com.ddudu.application.domain.ddudu.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DduduDomainService {

  public Ddudu create(User user, CreateDduduRequest request) {
    return Ddudu.builder()
        .userId(user.getId())
        .goalId(request.goalId())
        .name(request.name())
        .scheduledOn(request.scheduledOn())
        .build();
  }

}
