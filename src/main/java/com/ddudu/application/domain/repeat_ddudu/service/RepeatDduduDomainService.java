package com.ddudu.application.domain.repeat_ddudu.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.domain.enums.RepeatType;
import com.ddudu.application.dto.goal.request.CreateRepeatDduduRequestWithoutGoal;
import com.ddudu.application.dto.repeat_ddudu.RepeatPatternDto;
import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatDduduRequest;
import com.ddudu.application.dto.repeat_ddudu.request.UpdateRepeatDduduRequest;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class RepeatDduduDomainService {

  public RepeatDdudu create(CreateRepeatDduduRequest request) {
    return RepeatDdudu.builder()
        .name(request.name())
        .goalId(request.goalId())
        .startDate(request.startDate())
        .endDate(request.endDate())
        .repeatType(RepeatType.from(request.repeatType()))
        .repeatPatternDto(RepeatPatternDto.from(request))
        .beginAt(request.beginAt())
        .endAt(request.endAt())
        .build();
  }

  public RepeatDdudu create(Long goalId, CreateRepeatDduduRequestWithoutGoal request) {
    return RepeatDdudu.builder()
        .name(request.name())
        .goalId(goalId)
        .startDate(request.startDate())
        .endDate(request.endDate())
        .repeatType(RepeatType.from(request.repeatType()))
        .repeatPatternDto(RepeatPatternDto.from(request))
        .beginAt(request.beginAt())
        .endAt(request.endAt())
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

  public RepeatDdudu update(RepeatDdudu repeatDdudu, UpdateRepeatDduduRequest request) {
    RepeatPatternDto repeatPatternDto = new RepeatPatternDto(
        request.repeatDaysOfWeek(),
        request.repeatDaysOfMonth(),
        request.lastDayOfMonth()
    );

    return repeatDdudu.update(
        request.name(),
        RepeatType.from(request.repeatType()),
        repeatPatternDto,
        request.startDate(),
        request.endDate(),
        request.beginAt(),
        request.endAt()
    );
  }

}
