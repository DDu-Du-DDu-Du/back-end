package com.ddudu.application.domain.repeatable_ddudu.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.repeatable_ddudu.domain.RepeatPattern;
import com.ddudu.application.domain.repeatable_ddudu.domain.RepeatableDdudu;
import com.ddudu.application.domain.repeatable_ddudu.domain.enums.RepeatType;
import com.ddudu.application.dto.repeatable_ddudu.requset.CreateRepeatableDduduRequest;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class RepeatableDduduDomainService {

  public RepeatableDdudu create(CreateRepeatableDduduRequest request) {
    return RepeatableDdudu.builder()
        .name(request.name())
        .goalId(request.goalId())
        .startDate(request.startDate())
        .endDate(request.endDate())
        .repeatType(RepeatType.from(request.repeatType()))
        .repeatPattern(createRepeatPattern(request))
        .beginAt(request.beginAt())
        .endAt(request.endAt())
        .build();
  }

  public List<Ddudu> createRepeatedDdudus(Long userId, RepeatableDdudu repeatableDdudu) {
    return repeatableDdudu.getRepeatDates()
        .stream()
        .map(date -> Ddudu.builder()
            .name(repeatableDdudu.getName())
            .goalId(repeatableDdudu.getGoalId())
            .userId(userId)
            .repeatableDduduId(repeatableDdudu.getId())
            .scheduledOn(date)
            .beginAt(repeatableDdudu.getBeginAt())
            .endAt(repeatableDdudu.getEndAt())
            .build()
        )
        .toList();
  }

  private RepeatPattern createRepeatPattern(CreateRepeatableDduduRequest request) {
    return RepeatPattern.create(
        Objects.requireNonNull(RepeatType.from(request.repeatType())),
        request.repeatDaysOfWeek(),
        request.repeatDatesOfMonth(),
        request.lastDayOfMonth()
    );
  }

}
