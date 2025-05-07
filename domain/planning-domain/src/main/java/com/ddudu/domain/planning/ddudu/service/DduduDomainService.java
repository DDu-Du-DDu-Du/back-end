package com.ddudu.domain.planning.ddudu.service;

import com.ddudu.common.annotation.DomainService;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.dto.CreateDduduCommand;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DduduDomainService {

  public Ddudu create(Long userId, CreateDduduCommand command) {
    return Ddudu.builder()
        .userId(userId)
        .goalId(command.goalId())
        .name(command.name())
        .scheduledOn(command.scheduledOn())
        .build();
  }

}
