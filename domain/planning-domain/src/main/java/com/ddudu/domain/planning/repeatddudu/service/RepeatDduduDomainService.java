package com.ddudu.domain.planning.repeatddudu.service;

import com.ddudu.common.annotation.DomainService;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.RepeatPattern;
import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeatddudu.dto.CreateRepeatDduduCommand;
import com.ddudu.domain.planning.repeatddudu.dto.UpdateRepeatDduduCommand;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class RepeatDduduDomainService {

  public RepeatDdudu create(Long goalId, CreateRepeatDduduCommand command) {
    RepeatType repeatType = RepeatType.from(command.repeatType());
    RepeatPattern repeatPattern = repeatType.createRepeatPattern(command.repeatDaysOfWeek(),
        command.repeatDaysOfMonth(), command.lastDayOfMonth());
    Long goalIdFinal = Objects.nonNull(goalId) ?  goalId : command.goalId();

    return RepeatDdudu.builder()
        .name(command.name())
        .goalId(goalIdFinal)
        .startDate(command.startDate())
        .endDate(command.endDate())
        .repeatType(repeatType)
        .repeatPattern(repeatPattern)
        .beginAt(command.beginAt())
        .endAt(command.endAt())
        .build();
  }

  public List<Ddudu> createRepeatedDdudus(Long userId, RepeatDdudu repeatDdudu) {
    return repeatDdudu.getRepeatDates()
        .stream()
        .map(date -> Ddudu.builder()
            .name(repeatDdudu.getName())
            .goalId(repeatDdudu.getGoalId())
            .userId(userId)
            .repeatDduduId(repeatDdudu.getId())
            .scheduledOn(date)
            .beginAt(repeatDdudu.getBeginAt())
            .endAt(repeatDdudu.getEndAt())
            .build()
        )
        .toList();
  }

  public List<Ddudu> createRepeatedDdudusAfter(
      Long userId, RepeatDdudu repeatDdudu, LocalDateTime now
  ) {
    return repeatDdudu.getRepeatDates()
        .stream()
        .filter(date -> date.isAfter(ChronoLocalDate.from(now)))
        .map(date -> Ddudu.builder()
            .name(repeatDdudu.getName())
            .goalId(repeatDdudu.getGoalId())
            .userId(userId)
            .repeatDduduId(repeatDdudu.getId())
            .scheduledOn(date)
            .beginAt(repeatDdudu.getBeginAt())
            .endAt(repeatDdudu.getEndAt())
            .build()
        )
        .toList();
  }

  public RepeatDdudu update(RepeatDdudu repeatDdudu, UpdateRepeatDduduCommand command) {
    RepeatType repeatType = RepeatType.from(command.repeatType());
    RepeatPattern repeatPattern = repeatType.createRepeatPattern(command.repeatDaysOfWeek(),
        command.repeatDaysOfMonth(), command.lastDayOfMonth());

    return repeatDdudu.update(
        command.name(),
        repeatType,
        repeatPattern,
        command.startDate(),
        command.endDate(),
        command.beginAt(),
        command.endAt()
    );
  }

}
