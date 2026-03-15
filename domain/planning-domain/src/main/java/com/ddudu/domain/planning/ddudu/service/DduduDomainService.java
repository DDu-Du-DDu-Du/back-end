package com.ddudu.domain.planning.ddudu.service;

import com.ddudu.common.annotation.DomainService;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.dto.CreateDduduCommand;
import com.ddudu.domain.planning.ddudu.dto.UpdateDduduCommand;
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
        .beginAt(command.beginAt())
        .endAt(command.endAt())
        .remindDays(command.remindDays())
        .remindHours(command.remindHours())
        .remindMinutes(command.remindMinutes())
        .build();
  }

  public Ddudu update(Ddudu ddudu, UpdateDduduCommand command) {
    return ddudu.update(
        command.goalId(),
        command.name(),
        command.scheduledOn(),
        command.beginAt(),
        command.endAt(),
        command.remindDays(),
        command.remindHours(),
        command.remindMinutes()
    );
  }

}
